package org.pixelgaffer.turnierserver.backend;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.server.BackendFrontendConnectionHandler;
import org.pixelgaffer.turnierserver.backend.server.message.BackendFrontendCommandProcessed;
import org.pixelgaffer.turnierserver.backend.server.message.BackendFrontendResult;
import org.pixelgaffer.turnierserver.gamelogic.GameLogic;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Frontend;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import org.pixelgaffer.turnierserver.networking.messages.MessageForward;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Games
{
	@AllArgsConstructor
	static class FrontendWrapper implements Frontend
	{
		@Getter
		private int requestId;
		
		public void sendMessage (byte[] message) throws IOException
		{
			BackendFrontendConnectionHandler.getFrontend().sendMessage(message);
		}
	}
	
	private static final Object lock = new Games();
	
	/** Alle aktuell benutzten UUIDs. */
	private static final Set<UUID> uuids = new HashSet<>();
	
	/** Die zur UUID gehörende KI. */
	private static final Map<UUID, AiWrapper> aiWrappers = new HashMap<>();
	
	/** Die AI mit der angegebenen UUID hat sich disconnected. */
	public static void aiDisconnected (UUID uuid, WorkerConnection worker)
	{
		new Thread( () -> {
			synchronized (lock)
			{
				if (aiWrappers.containsKey(uuid))
				{
					worker.aiFinished();
				}
			}
			
			// die ki noch 1 min speichern um laggs in der verbindung zu
			// vermeiden
				try
				{
					Thread.sleep(60000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				finally
				{
					synchronized (lock)
					{
						aiWrappers.remove(uuid);
						uuids.remove(uuid);
					}
				}
			}).start();
	}
	
	/**
	 * Die AI mit der angegebenen UUID wurde von Backend/Worker/Sandbox beendet
	 * und das Spiel muss neu gestartet werden.
	 */
	public static void aiTerminated (UUID uuid) throws IOException, InstantiationException, IllegalAccessException
	{
		AiWrapper aiw;
		synchronized (lock)
		{
			aiw = getAiWrapper(uuid);
		}
		if (aiw == null)
		{
			BackendMain.getLogger().critical("Konnte KI mit der UUID " + uuid + " nicht finden");
			return;
		}
		GameImpl game = aiw.getGame();
		if (game == null)
		{
			BackendMain.getLogger().critical("Die KI " + uuid + " hat kein Spiel");
			return;
		}
		game.restart();
	}
	
	/** Das zur UUID gehörende Spiel. */
	private static final Map<UUID, GameImpl> games = new HashMap<>();
	
	/**
	 * Generiert eine aktuell freie UUID.
	 */
	private static UUID randomUuid ()
	{
		UUID uuid;
		synchronized (lock)
		{
			do
			{
				uuid = UUID.randomUUID();
			}
			while (uuids.contains(uuid));
			uuids.add(uuid);
		}
		return uuid;
	}
	
	/**
	 * Findet den AiWrapper mit der angegebenen UUID.
	 */
	public static AiWrapper getAiWrapper (UUID uuid)
	{
		synchronized (lock)
		{
			return aiWrappers.get(uuid);
		}
	}
	
	/**
	 * Lässt den AiWrapper mit der angegebenen UUID das Packet empfangen.
	 */
	public static void receiveMessage (UUID uuid, byte message[])
	{
		getAiWrapper(uuid).receiveMessage(message);
	}
	
	/**
	 * Lässt den im MessageForward angegebenen AiWrapper das Packet empfangen.
	 */
	public static void receiveMessage (MessageForward mf)
	{
		receiveMessage(mf.getAi(), mf.getMessage());
	}
	
	/**
	 * Diese Klasse ist die Implementation des Game-Interfaces der Game-Logic
	 * Bibliothek.
	 */
	static class GameImpl implements Game
	{
		static enum GameState
		{
			WAITING,
			STARTED,
			FINISHED
		}
		
		/** Die UUID dieses Spiels. */
		@Getter
		@NonNull
		private UUID uuid;
		
		/** Die ID dieses Spiels auf dem Datastore. */
		@Getter
		private int gameId;
		
		/** Die ID des Request vom Frontend. */
		@Getter
		private int requestId;
		
		/** Die Liste mit allen teilnehmenden KIs. */
		@Getter
		private List<AiWrapper> ais = new ArrayList<>();
		
		/** Die Spiellogik. */
		@Getter
		private GameLogic<?, ?> logic;
		
		/** Gibt an ob das Spiel schon gestartet wurde. */
		@Getter
		private GameState state = GameState.WAITING;
		
		private GameImpl (int gameId, @NonNull UUID uuid, int requestId, String[] languages, String ... ais) throws IOException
		{
			this.gameId = gameId;
			this.uuid = uuid;
			this.requestId = requestId;
			// die KIs erstellen
			for (int i = 0; i < ais.length; i++)
			{
				String ai = ais[i];
				// AiWrapper erstellen
				AiWrapper aiw = new AiWrapper(this, randomUuid(), languages[i]);
				aiw.setIndex(this.ais.size());
				// den String ai parsen (<ai-id>v<version>)
				String s[] = ai.split("v");
				aiw.setId(ai);
				aiw.setAiId(Integer.valueOf(s[0]));
				aiw.setVersion(Integer.valueOf(s[1]));
				// die KI zur Liste hinzufügen
				this.ais.add(aiw);
				synchronized (lock)
				{
					aiWrappers.put(aiw.getUuid(), aiw);
				}
				 
				// einen Worker mit der KI beauftragen
				WorkerConnection w = Workers.getStartableWorker(aiw.getLang());
				w.addJob(aiw, gameId);
				aiw.setConnection(w);
			}
		} 
		
		@Override
		public Frontend getFrontend ()
		{
			return new FrontendWrapper(getRequestId());
		}
		
		@Override
		public void finishGame () throws IOException
		{
			BackendMain.getLogger().info("finishGame() wurde für das Spiel " + getUuid() + " aufgerufen");
			for (AiWrapper ai : ais)
				ai.disconnect();
			if (state == GameState.STARTED) // ist beim restart auf false
			{
				BackendMain.getLogger().info("alle kis disconnected, sende success an frontend");
				BackendFrontendConnectionHandler.getFrontend().sendMessage(
						Parsers.getFrontend().parse(new BackendFrontendResult(getRequestId(), true)));
				synchronized (lock)
				{
					games.remove(getUuid());
					uuids.remove(getUuid());
				}
				
				state = GameState.FINISHED;
			}
		}
		
		/**
		 * Diese Methode wird aufgerufen wenn eine KI sich mit dem Worker
		 * verbunden hat. Wenn alle KIs verbunden sind wird das Spiel gestartet.
		 */
		public synchronized void aiConnected ()
		{
			if (state != GameState.WAITING)
				return;
			for (AiWrapper ai : ais)
				if (!ai.isConnected())
					return;
			BackendMain.getLogger().info("Alle KIs verbunden, starte Spiel " + getUuid());
			getLogic().startGame(this);
			state = GameState.STARTED;
			try
			{
				BackendFrontendConnectionHandler.getFrontend().sendMessage(
						Parsers.getFrontend().parse(new BackendFrontendCommandProcessed(getRequestId(), "started")));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		/**
		 * Diese Methode startet das Spiel neu.
		 */
		public synchronized void restart () throws IOException, InstantiationException, IllegalAccessException
		{
			if (state != GameState.STARTED)
			{
				BackendMain.getLogger().warning("Weigere mich nicht-laufendes Spiel " + getUuid() + " neu zu starten");
				return;
			}
			
			BackendMain.getLogger().info("Starte Spiel " + uuid + " neu");
			
			state = GameState.WAITING;
			logic = logic.getClass().newInstance();
			BackendFrontendConnectionHandler.getFrontend().sendMessage(
					Parsers.getFrontend().parse(new BackendFrontendCommandProcessed(getRequestId(), "restarted")));
			
			for (int i = 0; i < ais.size(); i++)
			{
				AiWrapper aiw = ais.get(i);
				if (aiw.getObject() != null)
					aiw.getObject().stop();
				aiw.disconnect();
				
				// da aiDisconnected aufgerufen wurde wird die alte UUID
				// entfernt
				aiw.setUuid(randomUuid());
				synchronized (lock)
				{
					aiWrappers.put(aiw.getUuid(), aiw);
				}
				
				// einen Worker mit der KI beauftragen
				WorkerConnection w = Workers.getStartableWorker(aiw.getLang());
				w.addJob(aiw, gameId);
				aiw.setConnection(w);
			}
		}
	}
	
	/**
	 * Lädt die Jar-Datei der GameLogic für das angegebene Spiel herunter, liest
	 * die Manifest-Datei, und lädt die GameLogic-Klasse.
	 */
	public static GameLogic<?, ?> loadGameLogic (int gameId) // keep in sync
																// with codr
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			ReflectiveOperationException, FTPListParseException
	{
		// jar runterladen
		File jar = Files.createTempFile("logic", ".jar").toFile();
		DatastoreFtpClient.retrieveGameLogic(gameId, jar);
		
		// manifest lesen
		JarFile jarFile = new JarFile(jar);
		Manifest mf = jarFile.getManifest();
		String classname = mf.getMainAttributes().getValue("Logic-Class");
		BackendMain.getLogger().info("Lade Logik-Klasse " + classname);
		String requiredLibs[] = mf.getMainAttributes().getValue("Required-Libs").split("\\s+");
		File libDir = Files.createTempDirectory("libs").toFile();
		for (String lib : requiredLibs)
			if (!lib.isEmpty())
				DatastoreFtpClient.retrieveLibrary(lib, "Java", libDir);
		jarFile.close();
		
		// klasse laden
		List<URL> urls = new ArrayList<>();
		urls.add(jar.toURI().toURL());
		for (File entry : libDir.listFiles())
			urls.add(entry.toURI().toURL());
		BackendMain.getLogger().todo("Hier ist ein ResourceLeak (auch im codr fixen)");
		@SuppressWarnings("resource")
		URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[0]));
		Class<?> clazz = cl.loadClass(classname);
		return (GameLogic<?, ?>)clazz.newInstance();
	}
	
	/**
	 * Startet ein Spiel des angegebenen Typs mit den angegebenen KIs.
	 */
	public static Game startGame (int gameId, int requestId, String[] languages, String ... ais)
			throws ReflectiveOperationException, IOException, FTPIllegalReplyException, FTPException,
			FTPDataTransferException, FTPAbortedException, FTPListParseException
	{
		GameLogic<?, ?> logic = loadGameLogic(gameId);
		UUID uuid = randomUuid();
		GameImpl game = new GameImpl(gameId, uuid, requestId, languages, ais);
		game.logic = logic;
		synchronized (lock)
		{
			games.put(uuid, game);
		}
		return game;
	}
	
	/**
	 * Startet ein Qualifikations-Spiel des angegebenen Typs mit der
	 * angegegebenen KI.
	 */
	public static Game startQualifyGame (int gameId, int requestId, String language, String ai)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			ReflectiveOperationException, FTPListParseException
	{
		GameLogic<?, ?> logic = loadGameLogic(gameId);
		UUID uuid = randomUuid();
		
		// die KIs herausfinden. QualiKI: -gameId version 1
		BackendMain.getLogger().todo("Nico muss mir die Anzahl der KIs sagen. Benutze default-Wert 2");
		int numAis = 2;
		String ais[] = new String[numAis];
		ais[0] = ai;
		for (int i = 1; i < numAis; i++)
			ais[1] = "-" + gameId + "v1";
		String[] languages = {language, "Java"};
		// Spiel starten
		GameImpl game = new GameImpl(gameId, uuid, requestId, languages, ais);
		game.logic = logic;
		synchronized (lock)
		{
			games.put(uuid, game);
		}
		return game;
	}
}

package org.pixelgaffer.turnierserver.backend;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

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
import org.pixelgaffer.turnierserver.backend.server.BackendFrontendCommandProcessed;
import org.pixelgaffer.turnierserver.backend.server.BackendFrontendConnectionHandler;
import org.pixelgaffer.turnierserver.backend.server.BackendFrontendResult;
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
	
	/** Alle aktuell benutzten UUIDs. */
	private static final Set<UUID> uuids = new HashSet<>();
	
	/** Die zur UUID gehörende KI. */
	private static final Map<UUID, AiWrapper> aiWrappers = new HashMap<>();
	
	/** Die AI mit der angegebenen UUID hat sich disconnected. */
	public static void aiDisconnected (UUID uuid)
	{
		aiWrappers.remove(uuid);
		uuids.remove(uuid);
	}
	
	/** Das zur UUID gehörende Spiel. */
	private static final Map<UUID, GameImpl> games = new HashMap<>();
	
	/**
	 * Generiert eine aktuell freie UUID.
	 */
	private static UUID randomUuid ()
	{
		UUID uuid;
		synchronized (uuids)
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
		return aiWrappers.get(uuid);
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
		private boolean started;
		
		private GameImpl (int gameId, @NonNull UUID uuid, int requestId, String ... ais) throws IOException
		{
			this.gameId = gameId;
			this.uuid = uuid;
			this.requestId = requestId;
			// die KIs erstellen
			for (String ai : ais)
			{
				// AiWrapper erstellen
				AiWrapper aiw = new AiWrapper(this, randomUuid());
				aiw.setIndex(this.ais.size());
				// den String ai parsen (<ai-id>v<version>)
				String s[] = ai.split("v");
				aiw.setId(ai);
				aiw.setAiId(Integer.valueOf(s[0]));
				aiw.setVersion(Integer.valueOf(s[1]));
				// die KI zur Liste hinzufügen
				this.ais.add(aiw);
				aiWrappers.put(aiw.getUuid(), aiw);
				
				// einen Worker mit der KI beauftragen
				WorkerConnection w = Workers.getStartableWorker();
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
			System.out.println("finishGame() wurde aufgerufen");
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			for (AiWrapper ai : ais)
				ai.disconnect();
			System.out.println("alle kis disconnected, sende success an frontend");
			BackendFrontendConnectionHandler.getFrontend().sendMessage(
					Parsers.getFrontend().parse(new BackendFrontendResult(getRequestId(), true)));
			games.remove(getUuid());
			uuids.remove(getUuid());
		}
		
		/**
		 * Diese Methode wird aufgerufen wenn eine KI sich mit dem Worker
		 * verbunden hat. Wenn alle KIs verbunden sind wird das Spiel gestartet.
		 */
		public synchronized void aiConnected ()
		{
			if (isStarted())
				return;
			for (AiWrapper ai : ais)
				if (!ai.isConnected())
					return;
			getLogic().startGame(this);
			started = true;
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
	}
	
	/**
	 * Lädt die Jar-Datei der GameLogic für das angegebene Spiel herunter, liest
	 * die Manifest-Datei, und lädt die GameLogic-Klasse.
	 */
	public static GameLogic<?, ?> loadGameLogic (int gameId) // keep in sync with codr
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			ReflectiveOperationException
	{
		// jar runterladen
		File jar = Files.createTempFile("logic", ".jar").toFile();
		DatastoreFtpClient.retrieveGameLogic(gameId, jar);
		
		// manifest lesen
		JarFile jarFile = new JarFile(jar);
		Manifest mf = jarFile.getManifest();
		String classname = mf.getMainAttributes().getValue("Logic-Class");
		System.out.println(classname);
		jarFile.close();
		
		// klasse laden
		@SuppressWarnings("resource")
		URLClassLoader cl = new URLClassLoader(new URL[] { jar.toURI().toURL() });
		Class<?> clazz = cl.loadClass(classname);
		return (GameLogic<?, ?>)clazz.newInstance();
	}
	
	/**
	 * Startet ein Spiel des angegebenen Typs mit den angegebenen KIs.
	 */
	public static Game startGame (int gameId, int requestId, String ... ais)
			throws ReflectiveOperationException, IOException, FTPIllegalReplyException, FTPException,
			FTPDataTransferException, FTPAbortedException
	{
		GameLogic<?, ?> logic = loadGameLogic(gameId);
		UUID uuid = randomUuid();
		GameImpl game = new GameImpl(gameId, uuid, requestId, ais);
		game.logic = logic;
		games.put(uuid, game);
		return game;
	}
	
	/**
	 * Startet ein Qualifikations-Spiel des angegebenen Typs mit der
	 * angegegebenen KI.
	 */
	public static Game startQualifyGame (int gameId, int requestId, String ai)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			ReflectiveOperationException
	{
		GameLogic<?, ?> logic = loadGameLogic(gameId);
		UUID uuid = randomUuid();
		
		// die KIs herausfinden. QualiKI: -gameId version 1
		System.out.println("Games:242: Nico muss mir die Anzahl der KIs sagen. Benutze default-Wert 2");
		int numAis = 2;
		String ais[] = new String[numAis];
		ais[0] = ai;
		for (int i = 1; i < numAis; i++)
			ais[1] = "-" + gameId + "v1";
		
		// Spiel starten
		GameImpl game = new GameImpl(gameId, uuid, requestId, ais);
		game.logic = logic;
		games.put(uuid, game);
		return game;
	}
}

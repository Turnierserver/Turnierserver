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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.pixelgaffer.turnierserver.PropertyUtils;
import org.pixelgaffer.turnierserver.backend.server.BackendFrontendConnectionHandler;
import org.pixelgaffer.turnierserver.gamelogic.GameLogic;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Frontend;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import org.pixelgaffer.turnierserver.networking.messages.MessageForward;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Games
{
	private static final Frontend f = (message) -> BackendFrontendConnectionHandler.getFrontend().sendMessage(message);
	
	/** Alle aktuell benutzten UUIDs. */
	private static final Set<UUID> uuids = new HashSet<>();
	
	/** Die zur UUID gehörende KI. */
	private static final Map<UUID, AiWrapper> aiWrappers = new HashMap<>();
	
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
		
		/** Die Liste mit allen teilnehmenden KIs. */
		@Getter
		private List<AiWrapper> ais = new ArrayList<>();
		
		/** Die Spiellogik. */
		@Getter
		private GameLogic<?, ?> logic;
		
		private GameImpl (int gameId, @NonNull UUID uuid, String ... ais) throws IOException
		{
			this.gameId = gameId;
			this.uuid = uuid;
			// die KIs erstellen
			for (String ai : ais)
			{
				// AiWrapper erstellen
				AiWrapper aiw = new AiWrapper(this, randomUuid());
				aiw.setIndex(this.ais.size());
				// den String ai parsen (<ai-id>v<version>)
				String s[] = ai.split("v");
				aiw.setId(Integer.valueOf(s[0]));
				aiw.setVersion(Integer.valueOf(s[1]));
				// die KI zur Liste hinzufügen
				this.ais.add(aiw);
				
				// einen Worker mit der KI beauftragen
				WorkerConnection w = Workers.getAvailableWorker();
				w.addJob(aiw, gameId);
			}
		}
		
		@Override
		public Frontend getFrontend ()
		{
			return f;
		}
		
		@Override
		public void finishGame ()
		{
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Lädt die Jar-Datei der GameLogic für das angegebene Spiel herunter, liest
	 * die Manifest-Datei, und lädt die GameLogic-Klasse.
	 */
	public static GameLogic<?, ?> loadGameLogic (int gameId)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			ClassNotFoundException, InstantiationException, IllegalAccessException
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
	public static Game startGame (int gameId, String ... ais)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException,
			FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
	{
		GameLogic<?, ?> logic = loadGameLogic(gameId);
		UUID uuid = randomUuid();
		GameImpl game = new GameImpl(gameId, uuid, ais);
		logic.startGame(game);
		return game;
	}
	
	public static void main (String args[]) throws Throwable
	{
		PropertyUtils.loadProperties("/etc/turnierserver/turnierserver.prop");
		System.out.println(startGame(1, "6v1", "6v1"));
	}
}

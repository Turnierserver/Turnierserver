package org.pixelgaffer.turnierserver.codr.simulator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import lombok.Getter;

import org.pixelgaffer.turnierserver.codr.CodrGame;
import org.pixelgaffer.turnierserver.codr.Version;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;
import org.pixelgaffer.turnierserver.gamelogic.GameLogic;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Frontend;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;

public class CodrGameImpl implements Game, Frontend
{
	/**
	 * Lädt die GameLogic und gibt eine Instanz davon zurück.
	 */
	public static GameLogic<?, ?> loadGameLogic (String game) // siehe
																// Games.loadGameLogic
																// im Backend
			throws IOException, ReflectiveOperationException
	{
		File jar = new File(Paths.gameLogic(game));
		
		// manifest lesen
		JarFile jarFile = new JarFile(jar);
		Manifest mf = jarFile.getManifest();
		String classname = mf.getMainAttributes().getValue("Logic-Class");
		jarFile.close();
		
		// klasse laden
		@SuppressWarnings("resource")
		URLClassLoader cl = new URLClassLoader(new URL[] { jar.toURI().toURL() });
		Class<?> clazz = cl.loadClass(classname);
		return (GameLogic<?, ?>)clazz.newInstance();
	}
	
	@Getter
	private boolean started;
	
	private Map<UUID, CodrAiWrapper> aiWrappers = new HashMap<>();
	
	public CodrAiWrapper getAi (UUID uuid)
	{
		return aiWrappers.get(uuid);
	}
	
	@Getter
	private List<CodrAiWrapper> ais = new ArrayList<>();
	
	UUID randomUUID ()
	{
		UUID uuid;
		do
		{
			uuid = UUID.randomUUID();
		}
		while (aiWrappers.containsKey(uuid));
		return uuid;
	}
	
	@Getter
	private GameLogic<?, ?> logic;
	
	private OutputStream renderData;
	
	public CodrGameImpl (CodrGame game, Collection<Version> opponents) throws IOException, ReflectiveOperationException
	{
		renderData = new FileOutputStream(Paths.gameRenderData(game));
		logic = loadGameLogic(game.logic);
		
		for (Version v : opponents)
		{
			CodrAiWrapper aiw = new CodrAiWrapper(this, randomUUID());
			aiw.setIndex(ais.size());
			aiw.setId(v.ai.title + "v" + v.number);
			aiw.setVersion(v);
			ais.add(aiw);
			aiWrappers.put(aiw.getUuid(), aiw);
		}
	}
	
	@Override
	public Frontend getFrontend ()
	{
		return this;
	}
	
	@Override
	public synchronized void finishGame () throws IOException
	{
		synchronized (renderData)
		{
			renderData.close();
		}
		for (CodrAiWrapper aiw : ais)
			aiw.disconnect();
	}
	
	public void startAis (Properties p) throws IOException
	{
		for (CodrAiWrapper aiw : ais)
		{
			p.put("turnierserver.ai.uuid", aiw.getUuid().toString());
			System.out.println(p);
			File f = Files.createTempFile("ai", ".prop").toFile();
			p.store(new FileOutputStream(f), null);
			aiw.executeAi(f.getAbsolutePath());
		}
	}
	
	public synchronized void aiConnected ()
	{
		if (!isStarted())
		{
			boolean start = true;
			for (CodrAiWrapper aiw : ais)
			{
				if (!aiw.isConnected())
				{
					start = false;
					break;
				}
			}
			if (start)
			{
				System.out.println("Alle KIs verbunden, das Spiel wird gestartet");
				logic.startGame(this);
				started = true;
			}
		}
	}
	
	// Frontend iface
	
	@Override
	public int getRequestId ()
	{
		return 0;
	}
	
	@Override
	public void sendMessage (byte[] message) throws IOException
	{
		synchronized (renderData)
		{
			renderData.write(message);
			renderData.flush();
		}
	}
}

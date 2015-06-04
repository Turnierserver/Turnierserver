package org.pixelgaffer.turnierserver.codr.simulator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		System.out.println(classname);
		jarFile.close();
		
		// klasse laden
		@SuppressWarnings("resource")
		URLClassLoader cl = new URLClassLoader(new URL[] { jar.toURI().toURL() });
		Class<?> clazz = cl.loadClass(classname);
		return (GameLogic<?, ?>)clazz.newInstance();
	}
	
	private Map<UUID, CodrAiWrapper> aiWrappers = new HashMap<>();
	
	@Getter
	private List<CodrAiWrapper> ais;
	
	UUID randomUUID ()
	{
		UUID uuid;
		do
		{
			uuid = UUID.randomUUID();
		} while (!aiWrappers.containsKey(uuid));
		return uuid;
	}
	
	@Getter
	private GameLogic<?, ?> logic;
	
	private OutputStream renderData;
	
	public CodrGameImpl (CodrGame game, Collection<Version> opponents) throws IOException
	{
		renderData = new FileOutputStream(Paths.gameRenderData(game));
		
		for (Version v : opponents)
		{
			CodrAiWrapper aiw = new CodrAiWrapper(this, randomUUID());
			aiw.setIndex(ais.size());
			aiw.setId(v.ai.title + "v" + v.number);
			aiw.setVersion(v);
			ais.add(aiw);
			aiWrappers.put(aiw.getUuid(), aiw);
			aiw.executeAi();
		}
	}
	
	@Override
	public Frontend getFrontend ()
	{
		return this;
	}
	
	@Override
	public void finishGame () throws IOException
	{
		synchronized (renderData)
		{
			renderData.close();
		}
		throw new UnsupportedOperationException();
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

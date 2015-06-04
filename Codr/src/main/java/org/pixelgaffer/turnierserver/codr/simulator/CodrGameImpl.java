package org.pixelgaffer.turnierserver.codr.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.pixelgaffer.turnierserver.codr.CodrAi;
import org.pixelgaffer.turnierserver.codr.CodrGame;
import org.pixelgaffer.turnierserver.codr.Version;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;
import org.pixelgaffer.turnierserver.gamelogic.GameLogic;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
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
	
	private SortedMap<UUID, CodrAiWrapper> ais = new TreeMap<>();
	
	private OutputStream renderData;
	
	public CodrGameImpl (CodrGame game, Collection<Version> opponents) throws FileNotFoundException
	{
		renderData = new FileOutputStream(Paths.gameRenderData(game));
		
		for (Version v : opponents)
		{
			CodrAi ai = v.ai;
			
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

	@Override
	public List<? extends Ai> getAis ()
	{
		return new ArrayList<CodrAiWrapper>(ais.values());
	}
}

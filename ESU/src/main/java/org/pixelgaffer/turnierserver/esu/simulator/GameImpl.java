package org.pixelgaffer.turnierserver.esu.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import lombok.Getter;

import org.pixelgaffer.turnierserver.esu.CodrGame;
import org.pixelgaffer.turnierserver.esu.utilities.Paths;
import org.pixelgaffer.turnierserver.gamelogic.GameLogic;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Frontend;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;

public class GameImpl implements Game, Frontend
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
	
	@Getter
	private List<? extends Ai> ais;
	
	private OutputStream renderData;
	
	public GameImpl (CodrGame game) throws FileNotFoundException
	{
		renderData = new FileOutputStream(Paths.gameRenderData(game));
	}

	@Override
	public Frontend getFrontend ()
	{
		return null;
	}

	@Override
	public void finishGame () throws IOException
	{
		renderData.close();
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
		
	}
}

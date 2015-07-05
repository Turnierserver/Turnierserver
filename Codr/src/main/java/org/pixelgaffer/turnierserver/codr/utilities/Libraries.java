package org.pixelgaffer.turnierserver.codr.utilities;

import java.io.File;

import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.compile.LibraryDownloader;

public class Libraries implements LibraryDownloader
{
	@Override
	public File[] getAiLibs (String language)
	{
		return new File(Paths.ailibrary(MainApp.actualGameType.get(), language)).listFiles();
	}
	
	@Override
	public File[] getLib (String language, String name)
	{
		File dir = new File(Paths.library(language, name));
		if (dir.exists())
			return dir.listFiles();
		if (!dir.mkdirs())
			throw new IllegalStateException("Can't download Library " + language + "/" + name + " to " + dir.getPath());
		if (!MainApp.webConnector.getLibrary(language, name))
			throw new IllegalStateException("Failed to download " + language + "/" + name);
		return dir.listFiles();
	}
}

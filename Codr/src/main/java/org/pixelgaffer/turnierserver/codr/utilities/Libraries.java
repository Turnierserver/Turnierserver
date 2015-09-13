/*
 * Libraries.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pixelgaffer.turnierserver.codr.utilities;

import java.io.File;

import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.compile.LibraryDownloader;


/**
 * Ist eine Klasse, die ermöglicht, einer KI bei Spielstart Bibliotheken zu übergeben.
 * 
 * @author Dominic
 */
public class Libraries implements LibraryDownloader {
	
	@Override
	public File[] getAiLibs(String language) {
		return new File(Paths.ailibrary(MainApp.actualGameType.get(), language)).listFiles();
	}
	
	
	@Override
	public File[] getLib(String language, String name) {
		File dir = new File(Paths.library(language, name));
		if (dir.exists())
			return dir.listFiles();
		if (!dir.mkdirs())
			throw new IllegalStateException("Can't download Library " + language + "/" + name + " to " + dir.getPath());
		if (!MainApp.webConnector.getLibrary(language, name)) {
			dir.delete();
			throw new IllegalStateException("Failed to download " + language + "/" + name);
		}
		return dir.listFiles();
	}


	@Override
	public LibraryDownloaderMode getMode ()
	{
		return LibraryDownloaderMode.EVERYTHING;
	}
}

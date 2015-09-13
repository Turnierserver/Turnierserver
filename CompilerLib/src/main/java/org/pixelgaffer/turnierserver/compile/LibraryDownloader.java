/*
 * LibraryDownloader.java
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
package org.pixelgaffer.turnierserver.compile;

import java.io.File;

public interface LibraryDownloader
{
	/**
	 * Der Modus des LibraryDownloaders.
	 */
	public enum LibraryDownloaderMode
	{
		/** Nur Bibliotheken, keine AiBibliotheken. */
		LIBS_ONLY,
		/** Bibliotheken und AiBibliotheken. */
		EVERYTHING;
	}
	
	/**
	 * Gibt den Modus des LibraryDownloaders zurück.
	 */
	public LibraryDownloaderMode getMode();
	
	/**
	 * Gibt alle Dateien zurück, die zur AiBibliothek gehören.
	 */
	public File[] getAiLibs(String language);
	
	/**
	 * Gibt die Datei mit dem angegebenen Namen der AiBibliothek zurück.
	 */
	public default File getAiLibFile (String language, String filename)
	{
		File[] files = getAiLibs(language);
		for (File file : files)
			if (file.getName().equals(filename))
				return file;
		return null;
	}
	
	/**
	 * Gibt alle Dateien zurück, die zu der Bibliothek mit dem angegebenen Namen gehören.
	 */
	public File[] getLib (String language, String name);
	
	/**
	 * Gibt die Datei mit dem angegebenen Namen der Datei zurück.
	 */
	public default File getFile (String language, String foldername, String filename)
	{
		File[] files = getLib(language, foldername);
		for (File file : files)
			if (file.getName().equals(filename))
				return file;
		return null;
	}
}

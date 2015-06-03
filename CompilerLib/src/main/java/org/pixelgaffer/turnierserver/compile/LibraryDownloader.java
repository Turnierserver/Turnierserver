package org.pixelgaffer.turnierserver.compile;

import java.io.File;

public interface LibraryDownloader
{
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
}

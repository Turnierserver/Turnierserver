package org.pixelgaffer.turnierserver.compile;

import java.io.File;

public interface LibraryDownloader
{
	/**
	 * Gibt alle Dateien zurück, die zur AiBibliothek gehören.
	 */
	public File[] getAiLibs(String language);
	
	/**
	 * Gibt alle Dateien zurück, die zu der Bibliothek mit dem angegebenen Namen gehören.
	 */
	public File[] getLib (String language, String name);
}

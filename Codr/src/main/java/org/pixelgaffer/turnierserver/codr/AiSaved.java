/*
 * AiSaved.java
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
package org.pixelgaffer.turnierserver.codr;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;


/**
 * Speichert eine KI lokal ab.
 * 
 * @author Philip
 */
public class AiSaved extends AiSimple {
	
	
	protected AiSaved(String ttitle, AiMode mmode) {
		super(ttitle, mmode);
	}
	
	
	public AiSaved(String ttitle) {
		super(ttitle, AiMode.saved);
		loadProps();
		loadVersions();
	}
	
	
	/**
	 * Speichert eine neue Ai mit dem übergebenen Titel und der Sprache ab.
	 * 
	 * @param tit der übergebene Titel
	 * @param lang die übergebene Sprache
	 */
	public AiSaved(String ttitle, String lang) {
		super(ttitle, AiMode.saved);
		
		language = lang;
		
		File dir = new File(Paths.ai(this));
		if (!dir.mkdirs()) {
			Dialog.error("Der Spieler existiert bereits.");
			description = "invalid";
		} else {
			storeProps();
		}
	}
	
	
	/**
	 * Fügt eine neue Version der Versionsliste hinzu.
	 * 
	 * @param type die Art, in der die Version hinzugefügt werden soll
	 * @return die Version, die hinzugefügt wurde
	 */
	public Version newVersion(NewVersionType type) {
		if (type == NewVersionType.fromFile) {
			return null;
		}
		return newVersion(type, "");
	}
	
	
	/**
	 * Fügt eine neue Version der Versionsliste hinzu.
	 * 
	 * @param type die Art, in der die Version hinzugefügt werden soll
	 * @param path der Pfad, von dem die Version kopiert werden soll, falls type==fromFile
	 * @return die Version, die hinzugefügt wurde
	 */
	public Version newVersion(NewVersionType type, String path) {
		Version version = null;
		switch (type) {
		case fromFile:
			version = new Version(this, versions.size(), mode, path);
			break;
		case lastVersion:
			if (versions.size() == 0) {
				return null;
			}
			version = new Version(this, versions.size(), mode, Paths.version(this, versions.size() - 1));
			break;
		case simplePlayer:
			version = new Version(this, versions.size(), mode);
			break;
		}
		versions.add(version);
		storeProps();
		return version;
	}
	
	
	/**
	 * Speichert die Eigenschaften des Players in das Dateiverzeichnis.
	 */
	public void storeProps() {
		
		Properties prop = new Properties();
		prop.setProperty("description", description);
		prop.setProperty("versionAmount", "" + versions.size());
		prop.setProperty("language", language.toString());
		prop.setProperty("gametype", gametype + "");
		
		try {
			Writer writer = new FileWriter(Paths.aiProperties(this));
			prop.store(writer, title);
			writer.close();
		} catch (IOException e) {
			ErrorLog.write("Es kann keine Properties-Datei angelegt werden. (Ai)");
		}
	}
	
	
	/**
	 * Setzt die Ai-Beschreibung.
	 * 
	 * @param des die Beschreibung des Players
	 */
	public void setDescription(String des) {
		description = des;
		storeProps();
	}
	
}

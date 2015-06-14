package org.pixelgaffer.turnierserver.codr;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;



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
			version = new Version(this, versions.size(), path);
			break;
		case lastVersion:
			if (versions.size() == 0) {
				return null;
			}
			version = new Version(this, versions.size(), Paths.version(this, versions.size() - 1));
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

package org.pixelgaffer.turnierserver.codr;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;
import org.pixelgaffer.turnierserver.codr.utilities.Resources;



public class AiSimple extends AiBase {
	
	
	
	/**
	 * Lädt eine Ai aus dem Dateisystem
	 * 
	 * @param tit der übergebene Titel
	 */
	protected AiSimple(String ttitle, AiMode mmode) {
		super(ttitle, mmode);
	}
	
	
	public AiSimple(String ttitle) {
		super(ttitle, AiMode.simplePlayer);
		loadProps();
		loadVersions();
	}
	
	
	
	/**
	 * Lädt aus dem Dateiverzeichnis die Eigenschaften des Players.
	 */
	public void loadProps() {
		try {
			Reader reader = new FileReader(Paths.aiProperties(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			gametype = prop.getProperty("gametype");
			description = prop.getProperty("description");
			language = prop.getProperty("language");
		} catch (IOException e) {
			ErrorLog.write("Fehler bei Laden aus der properties.txt");
		}
	}
	
	
	
	/**
	 * Lädt die Versionen aus dem Dateisystem, mit Hilfe der versionAmount-Property
	 */
	public void loadVersions() {
		versions.clear();
		int versionAmount = 0;
		try {
			Reader reader = new FileReader(Paths.aiProperties(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			versionAmount = Integer.parseInt(prop.getProperty("versionAmount"));
		} catch (IOException e) {
			ErrorLog.write("Fehler bei Laden aus der properties.txt (loadVerions)");
		}
		for (int i = 0; i < versionAmount; i++) {
			versions.add(new Version(this, i, mode));
		}
	}
	
	
	
	/**
	 * Gibt das gespeicherte Bild des Spielers zurück.
	 * 
	 * @return das gespeicherte Bild
	 */
	public ObjectProperty<Image> getPicture() {
		ObjectProperty<Image> img = new SimpleObjectProperty<>(Resources.imageFromFile(Paths.aiPicture(this)));
		if (img.get() == null) {
			img = new SimpleObjectProperty<>(Resources.defaultPicture());
		}
		return img;
	}
	
	
	/**
	 * Speichert das Bild des Spielers in der Datei picture.png.
	 * 
	 * @param img das zu speichernde Bild
	 */
	@Override public void setPicture(File file) {
		if (Paths.aiPicture(this) != null)
			new File(Paths.aiPicture(this)).delete();
		if (file.getName().equals("")){
			return;
		}
		
		String ending = null;
		if (file.getName().endsWith(".png"))
			ending = "png";
		if (file.getName().endsWith(".jpg"))
			ending = "jpg";
		if (file.getName().endsWith(".jpeg"))
			ending = "jpeg";
		if (file.getName().endsWith(".bmp"))
			ending = "bmp";
		if (file.getName().endsWith(".gif"))
			ending = "gif";
		
		if (ending == null){
			Dialog.error("Dieses Bildformat wird nicht unterstützt");
			return;
		}
		try {
			Files.copy(file.toPath(), new File(Paths.ai(this) + "/picture." + ending).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			ErrorLog.write("Bild konnte nicht kopiert werden");
		}
	}
	
}

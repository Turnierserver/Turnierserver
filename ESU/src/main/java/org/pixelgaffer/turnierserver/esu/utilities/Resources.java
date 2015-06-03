package org.pixelgaffer.turnierserver.esu.utilities;

import java.io.File;
import java.io.FileInputStream;

import javafx.scene.image.Image;

public class Resources {

	/**
	 * Gibt das Default-Bild für die KIs zurück
	 */
	public static Image defaultPicture(){
		try {
			return new Image(Paths.class.getResourceAsStream("../default_ai.png"));
		} catch (Exception ex) {
			ErrorLog.write("Default-Bild konnte nicht geladen werden.");
			return null;
		}
	}
	
	public static Image codr(){
		try {
			return new Image(Paths.class.getResourceAsStream("../Codr.png"));
		} catch (Exception ex) {
			ErrorLog.write("Default-Bild konnte nicht geladen werden.");
			return null;
		}
	}
	
	
	/**
	 * Gibt das Default-Bild für die KIs zurück
	 */
	public static Image imageFromFile(File file){
		try {
			return imageFromFile(file.getPath());
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * Gibt das Bild, das an der übergebenen Stelle gespeichert ist, zurück
	 */
	public static Image imageFromFile(String path){
		try {
			FileInputStream fin = new FileInputStream(path);
			Image img = new Image(fin);
			fin.close();
			return img;
		} catch (Exception e) {
			return null;
		}
	}
	
}

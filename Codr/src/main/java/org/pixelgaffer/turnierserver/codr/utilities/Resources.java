package org.pixelgaffer.turnierserver.codr.utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;



public class Resources {
	
	/**
	 * Gibt das Default-Bild für die KIs zurück
	 */
	public static Image defaultPicture() {
		try {
			// return new Image(Paths.class.getResourceAsStream("../default_ai.png"));
			return new Image(Paths.class.getResourceAsStream("../CodrIcon128.png"));
		} catch (Exception ex) {
			ErrorLog.write("Default-Bild konnte nicht geladen werden.");
			return null;
		}
	}
	
	
	public static Image codrIcon() {
		try {
			return new Image(Paths.class.getResourceAsStream("../CodrIcon128.png"));
		} catch (Exception ex) {
			ErrorLog.write("Default-Bild konnte nicht geladen werden.");
			return null;
		}
	}
	
	
	public static Image codr() {
		try {
			return new Image(Paths.class.getResourceAsStream("../Codr200.png"));
		} catch (Exception ex) {
			ErrorLog.write("Default-Bild konnte nicht geladen werden.");
			return null;
		}
	}
	
	
	/**
	 * Gibt das Default-Bild für die KIs zurück
	 */
	public static Image imageFromFile(File file) {
		try {
			return imageFromFile(file.getPath());
		} catch (Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Gibt das Bild, das an der übergebenen Stelle gespeichert ist, zurück
	 */
	public static Image imageFromFile(String path) {
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

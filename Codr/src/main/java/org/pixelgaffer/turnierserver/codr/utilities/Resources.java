package org.pixelgaffer.turnierserver.codr.utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javafx.scene.image.Image;

import org.apache.commons.io.IOUtils;


/**
 * übernimmt die Verwaltung von Resourcen, wie z.B. das Codr-Bild
 *
 * @author Philip
 */
public class Resources {
	
	/**
	 * Gibt das Default-Bild für die KIs zurück
	 */
	public static Image defaultPicture() {
		try {
			// return new Image(Paths.class.getResourceAsStream("default_ai.png"));
			return new Image(Paths.class.getResourceAsStream("CodrIcon128.png"));
		} catch (Exception ex) {
			ErrorLog.write("Default-Bild konnte nicht geladen werden. " + ex);
			return null;
		}
	}
	
	
	public static Image codrIcon() {
		try {
			return new Image(Paths.class.getResourceAsStream("CodrIcon128.png"));
		} catch (Exception ex) {
			ErrorLog.write("Default-Bild konnte nicht geladen werden. " + ex);
			return null;
		}
	}
	
	
	public static Image codr() {
		try {
			return new Image(Paths.class.getResourceAsStream("Codr200.png"));
		} catch (Exception ex) {
			ErrorLog.write("Default-Bild konnte nicht geladen werden. " + ex);
			return null;
		}
	}
	
	
	/**
	 * berechnet den Hash von zwei Dateien und vergleicht, ob sie identisch sind.
	 * @return true, wenn sie identisch sind
	 */
	public static boolean compareFiles(File f1, File f2) throws FileNotFoundException, IOException {
		MessageDigest comp1;
		MessageDigest comp2;
		try {
			comp1 = MessageDigest.getInstance("MD5");
			comp2 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			ErrorLog.write("Fatal Error: Der angegebene Hashalgorithmus existiert nicht.");
			e.printStackTrace();
			return false;
		}
		FileReader reader1 = new FileReader(f1);
		FileReader reader2 = new FileReader(f2);
		comp1.update(IOUtils.toByteArray(reader1));
		comp2.update(IOUtils.toByteArray(reader2));
		reader1.close();
		reader2.close();
		
		return Arrays.equals(comp1.digest(), comp2.digest());
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

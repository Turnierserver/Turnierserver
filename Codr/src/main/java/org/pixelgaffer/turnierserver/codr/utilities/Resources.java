package org.pixelgaffer.turnierserver.codr.utilities;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javafx.scene.image.Image;


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
	 * berechnet den MD5 Hash einer Datei und lädt sie herunter
	 */
	public static byte[] getHash(File file) throws FileNotFoundException, IOException {
		
		java.security.MessageDigest digest = null;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		InputStream in = new FileInputStream(file);
		byte[] buf = new byte[8192]; int read;
		while ((read = in.read(buf)) > 0)
		 digest.update(buf, 0, read);
		in.close();
		return digest.digest();
	}
	
	
	/**
	 * berechnet den Hash von zwei Dateien und vergleicht, ob sie identisch sind.
	 * @return true, wenn sie identisch sind
	 */
	public static boolean compareFiles(File f1, File f2) throws FileNotFoundException, IOException {
		return Arrays.equals(getHash(f1), getHash(f2));
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

package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.io.FileInputStream;

import javafx.scene.image.Image;

import org.pixelgaffer.turnierserver.esu.Player.Language;

public class Resources {
	
	/**
	 * Gibt den Pfad zum Spieler-Ordner zurück
	 */
	public static String playerFolder(){
		return "Players";
	}

	/**
	 * Gibt den Pfad zum Ordner eines bestimmten Spielers zurück
	 */
	public static String player(Player player){
		return "Players/" + player.title;
	}
	/**
	 * Gibt den Pfad zu den Properties eines Spielers zurück
	 */
	public static String playerProperties(Player player){
		return "Players/" + player.title + "/properties.txt";
	}
	/**
	 * Gibt den Pfad zum Bild eines Spielers zurück
	 */
	public static String playerPicture(Player player){
		return "Players/" + player.title + "/picture.png";
	}

	/**
	 * Gibt den Pfad zu einer bestimmten Version zurück
	 */
	public static String version(Version version){
		return "Players/" + version.player.title + "/v" + version.number;
	}
	/**
	 * Gibt den Pfad zu einer bestimmten Version zurück
	 */
	public static String version(Player player, int number){
		return "Players/" + player.title + "/v" + number;
	}
	/**
	 * Gibt den Pfad zu den Properties einer Version zurück
	 */
	public static String versionProperties(Version version){
		return "Players/" + version.player.title + "/v" + version.number + "/properties.txt";
	}
	/**
	 * Gibt den Pfad zu den Properties einer Version zurück
	 */
	public static String versionProperties(Player player, int number){
		return "Players/" + player.title + "/v" + number + "/properties.txt";
	}

	/**
	 * Gibt den Pfad zum SimplePlayer einer Sprache zurück
	 */
	public static String simplePlayer(Language language){
		return "Downloads/SimplePlayer/" + language.toString();
	}

	/**
	 * Gibt das Default-Bild für die KIs zurück
	 */
	public static Image defaultPicture(){
		try {
			return new Image(Resources.class.getResourceAsStream("default_ai.png"));
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

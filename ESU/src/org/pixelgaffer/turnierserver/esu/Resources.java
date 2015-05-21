package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.io.FileInputStream;

import javafx.scene.image.Image;

import org.pixelgaffer.turnierserver.esu.MainApp.Language;

public class Resources {

	public static String playerFolder(){
		return "Players";
	}
	
	public static String player(Player player){
		return "Players\\" + player.title;
	}
	public static String playerProperties(Player player){
		return "Players\\" + player.title + "\\properties.txt";
	}
	public static String playerPicture(Player player){
		return "Players\\" + player.title + "\\picture.png";
	}

	public static String version(Version version){
		return "Players\\" + version.player.title + "\\v" + version.number;
	}
	public static String versionProperties(Version version){
		return "Players\\" + version.player.title + "\\v" + version.number + "\\properties.txt";
	}
	public static String version(Player player, int number){
		return "Players\\" + player.title + "\\v" + number;
	}
	public static String versionProperties(Player player, int number){
		return "Players\\" + player.title + "\\v" + number + "\\properties.txt";
	}
	
	public static String simplePlayer(Language language){
		return "Downloads\\SimplePlayer\\" + language.toString();
	}
	
	public static Image defaultPicture(){
		try {
			return new Image(Resources.class.getResourceAsStream("default_ai.png"));
		} catch (Exception ex) {
			ErrorLog.write("Default-Bild konnte nicht geladen werden.");
			return null;
		}
	}
	
	public static Image imageFromFile(File file){
		try {
			return imageFromFile(file.getPath());
		} catch (Exception e) {
			return null;
		}
	}
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

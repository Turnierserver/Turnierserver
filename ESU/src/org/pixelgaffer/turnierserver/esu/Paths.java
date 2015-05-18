package org.pixelgaffer.turnierserver.esu;

import org.pixelgaffer.turnierserver.esu.MainApp.Language;

public class Paths {

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
	
	
}

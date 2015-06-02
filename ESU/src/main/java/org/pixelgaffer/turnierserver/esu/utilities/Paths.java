package org.pixelgaffer.turnierserver.esu.utilities;

import org.pixelgaffer.turnierserver.esu.Game;
import org.pixelgaffer.turnierserver.esu.ParticipantResult;
import org.pixelgaffer.turnierserver.esu.Player;
import org.pixelgaffer.turnierserver.esu.Player.Language;
import org.pixelgaffer.turnierserver.esu.Player.PlayerMode;
import org.pixelgaffer.turnierserver.esu.Version;

public class Paths {
	
	public static String sessionFile() {
		return "session.conf";
	}
	
	/**
	 * Gibt den Pfad zum Spieler-Ordner zurück
	 */
	public static String playerFolder(){
		return "Players";
	}
	/**
	 * Gibt den Pfad zum Spiele-Ordner zurück
	 */
	public static String gameFolder(){
		return "Players";
	}
	/**
	 * Gibt den Pfad zum SimplePlayer-Ordner zurück
	 */
	public static String simplePlayerFolder(){
		return "Downloads/SimplePlayer";
	}
	
	/**
	 * Gibt den Pfad zum Ordner eines bestimmten Spiels zurück
	 */
	public static String game(Game game){
		return gameFolder() + "/" + game.ID;
	}
	/**
	 * Gibt den Pfad zum Ordner eines bestimmten Spiels zurück
	 */
	public static String game(String id){
		return gameFolder() + "/" + id;
	}
	/**
	 * Gibt den Pfad zu den Properties eines bestimmten Spiels zurück
	 */
	public static String gameProperties(Game game){
		return gameFolder() + "/" + game.ID + "/properties.txt";
	}
	
	public static String participant(ParticipantResult part){
		return gameFolder() + "/" + part.game.ID + "/" + part.number + ".txt";
	}
	
	/**
	 * Gibt den Pfad zum Ordner eines bestimmten Spielers zurück
	 */
	public static String player(Player player){
		if (player.mode == PlayerMode.saved){
			return playerFolder() + "/" + player.title;
		}
		else if (player.mode == PlayerMode.simplePlayer){
			return simplePlayerFolder() + "/" + player.title;
		}
		else{
			ErrorLog.write("Es wurde ein Pfad zu einem nicht gespeicherten Player angefordert");
			return null;
		}
	}
	/**
	 * Gibt den Pfad zu den Properties eines Spielers zurück
	 */
	public static String playerProperties(Player player){
		return player(player) + "/properties.txt";
	}
	/**
	 * Gibt den Pfad zum Bild eines Spielers zurück
	 */
	public static String playerPicture(Player player){
		return player(player) + "/picture.png";
	}

	/**
	 * Gibt den Pfad zu einer bestimmten Version zurück
	 */
	public static String version(Version version){
		return player(version.player) + "/v" + version.number;
	}
	/**
	 * Gibt den Pfad zu einer bestimmten Version zurück
	 */
	public static String version(Player player, int number){
		return player(player) + "/v" + number;
	}
	/**
	 * Gibt den Pfad zu den Properties einer Version zurück
	 */
	public static String versionProperties(Version version){
		return version(version) + "/properties.txt";
	}
	/**
	 * Gibt den Pfad zu den Properties einer Version zurück
	 */
	public static String versionProperties(Player player, int number){
		return version(player, number) + "/properties.txt";
	}

	/**
	 * Gibt den Pfad zum SimplePlayer einer Sprache zurück
	 */
	public static String simplePlayer(Language language){
		return simplePlayerFolder() + "/SimplePlayer" + language.toString() + "/v0";
	}

	
}

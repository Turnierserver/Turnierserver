package org.pixelgaffer.turnierserver.codr.utilities;


import java.io.File;

import org.pixelgaffer.turnierserver.codr.AiBase;
import org.pixelgaffer.turnierserver.codr.AiBase.AiMode;
import org.pixelgaffer.turnierserver.codr.AiExtern;
import org.pixelgaffer.turnierserver.codr.GameBase;
import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.codr.ParticipantResult;
import org.pixelgaffer.turnierserver.codr.Version;



public class Paths {
	
	public static String aceFolder() {
		return "Ace";
	}
	
	
	public static String settings() {
		return "settings.txt";
	}
	
	
	public static String downloadFolder() {
		return "Downloads";
	}
	
	
	public static String sessionFile() {
		return "session.conf";
	}
	
	
	public static String langsFile() {
		return downloadFolder() + "/langs.prop";
	}
	
	
	public static String gameTypesFile() {
		return downloadFolder() + "/gametypes.prop";
	}
	
	
	public static String downloadGameType(String game) {
		return downloadFolder() + "/" + game;
	}
	
	
	public static String gameLogic(String game) {
		return downloadGameType(game) + "/gamelogic.jar";
	}
	
	
	/**
	 * Gibt den Pfad zum SimplePlayer-Ordner zurück
	 */
	public static String simplePlayerFolder(String game) {
		return downloadGameType(game) + "/SimplePlayer";
	}
	
	
	public static String simplePlayer(String game, String language) {
		return downloadGameType(game) + "/SimplePlayer/SimplePlayer" + language + "/v0";
	}
	
	
	public static String ailibrary(String game, String language) {
		return downloadGameType(game) + "/ailib/" + language;
	}
	
	
	/**
	 * Gibt den Pfad zum Spieler-Ordner zurück
	 */
	public static String aiFolder() {
		return "AIs/Intern";
	}
	
	
	public static String aiExternFolder() {
		return "AIs/Extern";
	}
	
	
	/**
	 * Gibt den Pfad zum Spiele-Ordner zurück
	 */
	public static String gameFolder() {
		return "Games";
	}
	
	
	/**
	 * Gibt den Pfad zum Ordner eines bestimmten Spiels zurück
	 */
	public static String game(GameBase game) {
		return gameFolder() + "/" + game.ID;
	}
	
	
	/**
	 * Gibt den Pfad zum Ordner eines bestimmten Spiels zurück
	 */
	public static String game(String id) {
		return gameFolder() + "/" + id;
	}
	
	
	/**
	 * Gibt den Pfad zu den Properties eines bestimmten Spiels zurück
	 */
	public static String gameProperties(GameBase game) {
		return game(game) + "/gameProperties.txt";
	}
	
	
	/**
	 * Gibt den Pfad zu dem Output der GameLogic für das Spiel zurück.
	 */
	public static String gameRenderData(GameBase game) {
		return game(game) + "/renderData.txt";
	}
	
	
	public static String participant(ParticipantResult part) {
		return gameFolder() + "/" + part.game.ID + "/" + part.number + ".txt";
	}
	
	
	/**
	 * Gibt den Pfad zum Ordner eines bestimmten Spielers zurück
	 */
	public static String ai(AiBase ai) {
		if (ai.mode == AiMode.simplePlayer) {
			return simplePlayerFolder(MainApp.actualGameType.get()) + "/" + ai.title;
		} else if (ai.mode == AiMode.extern) {
			return aiExternFolder() + "/" + ai.title;
		} else {
			return aiFolder() + "/" + ai.title;
		}
	}
	
	
	/**
	 * Gibt den Pfad zu den Properties eines Spielers zurück
	 */
	public static String aiProperties(AiBase ai) {
		return ai(ai) + "/aiProperties.txt";
	}
	
	
	/**
	 * Gibt den Pfad zum Bild eines Spielers zurück
	 */
	public static String aiPicture(AiBase ai) {
		if (new File(ai(ai) + "/picture.png").exists())
			return ai(ai) + "/picture.png";
		else if (new File(ai(ai) + "/picture.jpg").exists())
			return ai(ai) + "/picture.jpg";
		else if (new File(ai(ai) + "/picture.jpeg").exists())
			return ai(ai) + "/picture.jpeg";
		else if (new File(ai(ai) + "/picture.gif").exists())
			return ai(ai) + "/picture.gif";
		else if (new File(ai(ai) + "/picture.bmp").exists())
			return ai(ai) + "/picture.bmp";
		else
			return null;
	}
	
	
	/**
	 * Gibt den Pfad zu einer bestimmten Version zurück
	 */
	public static String version(Version version) {
		if (version.ai.mode == AiMode.extern)
			return ((AiExtern) version.ai).path;
		else
			return ai(version.ai) + "/v" + version.number;
	}
	
	
	
	/**
	 * Gibt den Pfad zu einer bestimmten Version zurück
	 */
	public static String version(AiBase ai, int number) {
		if (ai.mode == AiMode.extern)
			return ((AiExtern) ai).path;
		else
			return ai(ai) + "/v" + number;
	}
	
	
	/**
	 * Gibt den Pfad zu den Properties einer Version zurück
	 */
	public static String versionProperties(Version version) {
		if (version.ai.mode == AiMode.extern)
			return ai(version.ai) + "/versionProperties.txt";
		else
			return version(version) + "/versionProperties.txt";
	}
	
	
	public static String versionSrc(Version version) {
		return version(version) + "/src";
	}
	
	
	public static String versionSrcStartClass(Version version) {
		return versionSrc(version) + "/settings.prop";
	}
	
	
	public static String versionBin(Version version) {
		if (version.ai.mode == AiMode.extern)
			return ai(version.ai) + "/bin";
		else
			return version(version) + "/bin";
	}
}

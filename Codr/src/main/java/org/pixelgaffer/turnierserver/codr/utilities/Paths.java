package org.pixelgaffer.turnierserver.codr.utilities;

import java.io.File;

import org.pixelgaffer.turnierserver.codr.AiBase;
import org.pixelgaffer.turnierserver.codr.AiBase.AiMode;
import org.pixelgaffer.turnierserver.codr.AiExtern;
import org.pixelgaffer.turnierserver.codr.GameBase;
import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.codr.ParticipantResult;
import org.pixelgaffer.turnierserver.codr.Version;


/**
 * Sammelt alle Pfade zu Dateien, die außerhalb von Codr verwendet werden.
 * 
 * @author Philip
 */
public class Paths {
	
	/**
	 * wird für den Editor gebraucht.
	 */
	public static String syntaxFolder() {
		return "Syntax";
	}
	
	
	/**
	 * Hier wird eine neue Version von Codr abgespeichert, falls sie heruntergeladen wird.
	 */
	public static String newCodrVersion() {
		return "CodrNewVersion.jar";
	}
	
	
	/**
	 * Hier werden alle Einstellungen gespeichert, die im Main-Fenster vorgenommen werden.
	 */
	public static String settings() {
		return "settings.txt";
	}
	
	
	/**
	 * der Ordner mit den Downloads.
	 */
	public static String downloadFolder() {
		return "Downloads";
	}
	
	
	/**
	 * der Cookie für die Anmeldung
	 */
	public static String sessionFile() {
		return "session.conf";
	}
	
	
	/**
	 * eine Liste aller verfügbarer Programmiersprachen
	 */
	public static String langsFile() {
		return downloadFolder() + "/langs.prop";
	}
	
	
	/**
	 * eine Liste aller verfügbarer Spieltypen
	 */
	public static String gameTypesFile() {
		return downloadFolder() + "/gametypes.prop";
	}
	
	
	/**
	 * hier werden die Spieltypen gespeichert
	 */
	public static String downloadGameType(String game) {
		return downloadFolder() + "/" + game;
	}
	
	
	/**
	 * hier werden die Spieltypen gespeichert
	 */
	public static String gameLogic(String game) {
		return downloadGameType(game) + "/gamelogic.jar";
	}
	
	
	/**
	 * hier werden die Bibliotheken gespeichert
	 */
	public static String downloadLibraries() {
		return downloadFolder() + "/Libraries";
	}
	
	
	/**
	 * gibt den ordner der bibliothek zurück. <code>name</code> ist name/version.
	 */
	public static String library(String language, String name) {
		return downloadLibraries() + "/" + language + "/" + name;
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
	 * Gibt den Pfad zum KI-Ordner zurück
	 */
	public static String aiFolder() {
		return "AIs/Intern";
	}
	
	
	/**
	 * hier werden die Einträge zu externen KIs gespeichert
	 */
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
	public static String game(int id) {
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
	
	
	/**
	 * der Pfad zu einem ParticipantResult von einem Spiel
	 */
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
	
	
	/**
	 * der Pfad zu dem Source-Ordner einer Version
	 */
	public static String versionSrc(Version version) {
		return version(version) + "/src";
	}
	
	
	/**
	 * der Pfad zu den Settings.prop einer Version
	 */
	public static String versionSettingsProp(Version version) {
		return versionSrc(version) + "/settings.prop";
	}
	
	
	/**
	 * pfad zu den Binaries einer Version
	 */
	public static String versionBin(Version version) {
		if (version.ai.mode == AiMode.extern)
			return ai(version.ai) + "/bin";
		else
			return version(version) + "/bin";
	}
}

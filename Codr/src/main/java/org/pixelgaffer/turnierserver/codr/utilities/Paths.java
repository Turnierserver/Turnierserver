package org.pixelgaffer.turnierserver.codr.utilities;

import org.pixelgaffer.turnierserver.codr.Ai;
import org.pixelgaffer.turnierserver.codr.CodrGame;
import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.codr.ParticipantResult;
import org.pixelgaffer.turnierserver.codr.Version;
import org.pixelgaffer.turnierserver.codr.Ai.AiMode;

public class Paths {
	
	public static String aceFolder() {
		return "Ace";
	}
	
	public static String settings(){
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
	public static String simplePlayerFolder(String game){
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
	public static String aiFolder(){
		return "AIs";
	}
	/**
	 * Gibt den Pfad zum Spiele-Ordner zurück
	 */
	public static String gameFolder(){
		return "Games";
	}
	
	/**
	 * Gibt den Pfad zum Ordner eines bestimmten Spiels zurück
	 */
	public static String game(CodrGame game){
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
	public static String gameProperties(CodrGame game){
		return game(game) + "/properties.txt";
	}
	
	/**
	 * Gibt den Pfad zu dem Output der GameLogic für das Spiel zurück. 
	 */
	public static String gameRenderData (CodrGame game)
	{
		return game(game) + "/renderData.txt";
	}
	
	public static String participant(ParticipantResult part){
		return gameFolder() + "/" + part.game.ID + "/" + part.number + ".txt";
	}
	
	/**
	 * Gibt den Pfad zum Ordner eines bestimmten Spielers zurück
	 */
	public static String ai(Ai ai){
		if (ai.mode == AiMode.saved){
			return aiFolder() + "/" + ai.title;
		}
		else if (ai.mode == AiMode.simplePlayer){
			return simplePlayerFolder(MainApp.actualGameType.get()) + "/" + ai.title;
		}
		else{
			ErrorLog.write("Es wurde ein Pfad zu einem nicht gespeicherten Ai angefordert");
			return null;
		}
	}
	/**
	 * Gibt den Pfad zu den Properties eines Spielers zurück
	 */
	public static String aiProperties(Ai ai){
		return ai(ai) + "/properties.txt";
	}
	/**
	 * Gibt den Pfad zum Bild eines Spielers zurück
	 */
	public static String aiPicture(Ai ai){
		return ai(ai) + "/picture.png";
	}

	/**
	 * Gibt den Pfad zu einer bestimmten Version zurück
	 */
	public static String version(Version version){
		return ai(version.ai) + "/v" + version.number;
	}
	/**
	 * Gibt den Pfad zu einer bestimmten Version zurück
	 */
	public static String version(Ai ai, int number){
		return ai(ai) + "/v" + number;
	}
	/**
	 * Gibt den Pfad zu den Properties einer Version zurück
	 */
	public static String versionProperties(Version version){
		return version(version) + "/properties.txt";
	}
	
	public static String versionSrc(Version version){
		return version(version) + "/src";
	}
	
	public static String versionSrcStartClass(Version version){
		return versionSrc(version) + "/settings.prop";
	}

	public static String versionBin(Version version){
		return version(version) + "/bin";
	}
}

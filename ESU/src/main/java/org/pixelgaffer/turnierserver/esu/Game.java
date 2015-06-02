package org.pixelgaffer.turnierserver.esu;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.json.JSONObject;
import org.pixelgaffer.turnierserver.esu.utilities.*;

public class Game {
	
	public final GameMode mode;
	public String ID = null;
	public String date;
	public String duration;
	public String logic;
	public String state;
	public String judged;
	public ObservableList<ParticipantResult> participants = FXCollections.observableArrayList();
	
	
	public static enum GameMode{
		playing, saved, onlineLoaded
	}
	
	public Game(String id){
		mode = GameMode.saved;
		ID = id;
		loadProps();
	}
	public Game(String llogic, String sstate){
		mode = GameMode.playing;
		logic = llogic;
		state = sstate;
		judged = "Nein";
		storeProps();
	}
	public Game(JSONObject json){
		mode = GameMode.onlineLoaded;
		////////////////////////Nico: hier kommt dein Code rein/////////////////////////////////////////////////////
	}
	
	
	public void loadProps(){
		if (mode != GameMode.saved && mode != GameMode.playing){
			ErrorLog.write("dies ist kein lesbares Objekt (Game.loadProps)");
			return;
		}
		try {
			Reader reader = new FileReader(Paths.gameProperties(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			date = prop.getProperty("date");
			duration = prop.getProperty("duration");
			logic = prop.getProperty("logic");
			state = prop.getProperty("state");
			judged = prop.getProperty("judged");
			
			int amount = Integer.parseInt(prop.getProperty("participantAmount"));
			for (int i = 0; i < amount; i++){
				participants.get(i).playerName.set(prop.getProperty("playerName" + participants.get(i).number));
				participants.get(i).kiName.set(prop.getProperty("kiName" + participants.get(i).number));
				participants.get(i).duration.set(prop.getProperty("duration" + participants.get(i).number));
				participants.get(i).moveCount.set(prop.getProperty("moveCount" + participants.get(i).number));
				participants.get(i).points.set(prop.getProperty("points" + participants.get(i).number));
				participants.get(i).won.set(prop.getProperty("won" + participants.get(i).number));
			}
			
		} catch (IOException e) {ErrorLog.write("Fehler bei Laden aus der properties.txt (Game)");}
	}
	public void storeProps(){
		if (mode != GameMode.playing){
			ErrorLog.write("dies ist kein speicherbares Objekt (Game.loadProps)");
			return;
		}
		
		if (ID == null){
			getNewID();
		}
		
		Properties prop = new Properties();
		prop.setProperty("date", date);
		prop.setProperty("duration", duration);
		prop.setProperty("logic", logic);
		prop.setProperty("state", state);
		prop.setProperty("judged", judged);
		
		prop.setProperty("participantAmount", participants.size() + "");
		for (int i = 0; i < participants.size(); i++){
			prop.setProperty("playerName" + participants.get(i).number.get(), participants.get(i).playerName.get());
			prop.setProperty("kiName" + participants.get(i).number.get(), participants.get(i).kiName.get());
			prop.setProperty("duration" + participants.get(i).number.get(), participants.get(i).duration.get());
			prop.setProperty("moveCount" + participants.get(i).number.get(), participants.get(i).moveCount.get());
			prop.setProperty("points" + participants.get(i).number.get(), participants.get(i).points.get());
			prop.setProperty("won" + participants.get(i).number.get(), participants.get(i).won.get());
		}
		
		try {
			File dir = new File(Paths.game(this));
			dir.mkdirs();
			
			Writer writer = new FileWriter(Paths.gameProperties(this));
			prop.store(writer, ID);
			writer.close();
		} catch (IOException e) {ErrorLog.write("Es kann keine Properties-Datei angelegt werden. (Game)");}
	}
	
	/**
	 * Setzt den date-String auf die aktuelle Zeit
	 */
	public void setDateNow(){
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm,ss");
		date = format.format(now);
	}
	
	public void getNewID(){
		for (int i = 1; i < 10000; i++){
			File dir = new File(Paths.game("Game" + i));
			if (dir.mkdirs()){
				ID = "Game" + i;
				return;
			}
		}
		ErrorLog.write("GetNewID-ERROR: Mehr als 10.000 Spielordner wurden ausprobiert: Möglicherweise gibt es keine Zugriffsberechtigung.");
	}
	
	public void play(List<Version> opponents){
		for (int i = 0; i < opponents.size(); i++){
			participants.add(new ParticipantResult(this, "Lokal", opponents.get(i).ai.title + "v" + opponents.get(i).number, "100ms", "5", "20", "Ja"));
		}
		setDateNow();
		duration = "500ms";
	}
	
	
	
}

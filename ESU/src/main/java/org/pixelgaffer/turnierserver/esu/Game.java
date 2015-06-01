package org.pixelgaffer.turnierserver.esu;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.pixelgaffer.turnierserver.esu.utilities.*;

public class Game {
	
	public String ID = null;
	public String date;
	public String duration;
	public String logic;
	public String state;
	public String judged;
	public ObservableList<ParticipantResult> participants = FXCollections.observableArrayList();
	public boolean isOnline;  //muss nicht gespeichert werden
	
	
	public Game(String id){
		ID = id;
		isOnline = false;
	}
	public Game(String llogic, String sstate){
		logic = llogic;
		state = sstate;
		judged = "Nein";
		isOnline = false;
	}
	public Game(String llogic, String sstate, String jjudged, String ddate, String dduration, ObservableList<ParticipantResult> pparticipants){
		logic = llogic;
		state = sstate;
		judged = jjudged;
		date = ddate;
		duration = dduration;
		participants = pparticipants;
		isOnline = true;
	}
	
	
	public void loadProps(){
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
				participants.get(i).playerName = prop.getProperty("playerName" + participants.get(i).number);
				participants.get(i).kiName = prop.getProperty("kiName" + participants.get(i).number);
				participants.get(i).duration = prop.getProperty("duration" + participants.get(i).number);
				participants.get(i).moveCount = prop.getProperty("moveCount" + participants.get(i).number);
				participants.get(i).points = prop.getProperty("points" + participants.get(i).number);
				participants.get(i).won = prop.getProperty("won" + participants.get(i).number);
			}
			
		} catch (IOException e) {ErrorLog.write("Fehler bei Laden aus der properties.txt (Game)");}
	}
	public void storeProps(){
		if (isOnline)
			return;
		
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
			prop.setProperty("playerName" + participants.get(i).number, participants.get(i).playerName);
			prop.setProperty("kiName" + participants.get(i).number, participants.get(i).kiName);
			prop.setProperty("duration" + participants.get(i).number, participants.get(i).duration);
			prop.setProperty("moveCount" + participants.get(i).number, participants.get(i).moveCount);
			prop.setProperty("points" + participants.get(i).number, participants.get(i).points);
			prop.setProperty("won" + participants.get(i).number, participants.get(i).won);
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
		ErrorLog.write("Mehr als 10.000 Spielordner wurden ausprobiert: Möglicherweise gibt es keine Zugriffsberechtigung.");
	}
	
	public void play(List<Version> opponents){
		for (int i = 0; i < opponents.size(); i++){
			participants.add(new ParticipantResult(this, "Lokal", opponents.get(i).player.title + "v" + opponents.get(i).number, "100ms", "5", "20", "Ja"));
		}
		setDateNow();
		duration = "500ms";
	}
	
	
	
}

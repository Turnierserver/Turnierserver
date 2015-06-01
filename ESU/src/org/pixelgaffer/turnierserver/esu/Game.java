package org.pixelgaffer.turnierserver.esu;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import utilities.*;

public class Game {
	
	public String ID = "-1";  //-1: nicht gesetzt
	public String date;
	public String duration;
	public String logic;
	public String state;
	public String judged;
	public List<ParticipantResult> participants = new ArrayList<ParticipantResult>();
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
	public Game(String llogic, String sstate, String jjudged, String ddate, String dduration, List<ParticipantResult> pparticipants){
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
			Reader reader = new FileReader(Resources.gameProperties(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			date = prop.getProperty("date");
			duration = prop.getProperty("duration");
			logic = prop.getProperty("logic");
			state = prop.getProperty("state");
			judged = prop.getProperty("judged");
		} catch (IOException e) {ErrorLog.write("Fehler bei Laden aus der properties.txt (Version)");}
	}
	public void storeProps(){
		if (isOnline)
			return;
		
		Properties prop = new Properties();
		prop.setProperty("date", date);
		prop.setProperty("duration", duration);
		prop.setProperty("logic", logic);
		prop.setProperty("state", state);
		prop.setProperty("judged", judged);
		
		try {
			File dir = new File(Resources.game(this));
			dir.mkdirs();
			
			Writer writer = new FileWriter(Resources.gameProperties(this));
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
	
	
	
	
	
}

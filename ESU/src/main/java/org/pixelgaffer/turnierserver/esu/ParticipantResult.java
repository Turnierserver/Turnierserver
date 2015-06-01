package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Resources;

public class ParticipantResult {
	
	public final Game game;
	public String number;
	public String playerName;
	public String kiName;
	public String duration;
	public String moveCount;
	public String points;
	public String won;
	
	public ParticipantResult(Game ggame, String pplayerName, String kkiName, String dduration, String mmoveCount, String ppoints, String wwon){
		game = ggame;
		playerName = pplayerName;
		kiName = kkiName;
		duration = dduration;
		moveCount = mmoveCount;
		points = ppoints;
		won = wwon;
	}
	
	
	

	public void loadProps(){
		try {
			Reader reader = new FileReader(Resources.participant(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			playerName = prop.getProperty("playerName");
			kiName = prop.getProperty("kiName");
			duration = prop.getProperty("duration");
			moveCount = prop.getProperty("moveCount");
			points = prop.getProperty("points");
			won = prop.getProperty("won");
		} catch (IOException e) {ErrorLog.write("Fehler bei Laden aus der properties.txt (Participant)");}
	}
	public void storeProps(){
		Properties prop = new Properties();
		prop.setProperty("playerName", playerName);
		prop.setProperty("kiName", kiName);
		prop.setProperty("duration", duration);
		prop.setProperty("moveCount", moveCount);
		prop.setProperty("points", points);
		prop.setProperty("won", won);
		
		try {
			Writer writer = new FileWriter(Resources.participant(this));
			prop.store(writer, number);
			writer.close();
		} catch (IOException e) {ErrorLog.write("Es kann keine Properties-Datei angelegt werden. (Participant)");}
	}
	
}

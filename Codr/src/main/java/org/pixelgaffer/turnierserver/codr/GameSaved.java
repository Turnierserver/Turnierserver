package org.pixelgaffer.turnierserver.codr;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.pixelgaffer.turnierserver.codr.simulator.CodrAiServer;
import org.pixelgaffer.turnierserver.codr.simulator.CodrGameImpl;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;


/**
 * Stellt ein lokal gespeichertes Spiel dar.
 * 
 * @author Philip
 */
public class GameSaved extends GameBase {
	
	/**
	 * wird verwendet, wenn ein neues Spiel gestartet wird
	 * 
	 * @param llogic
	 */
	public GameSaved(String llogic) {
		super(GameMode.playing);
		getNewID();
	}
	
	
	/**
	 * wird verwendet, wenn der GameManager das Spiel aus dem Dateisystem lädt.
	 * 
	 * @param iid
	 */
	public GameSaved(int iid) {
		super(GameMode.saved);
		ID = iid;
		loadProps();
	}
	
	
	public void loadProps() {
		if (mode != GameMode.saved && mode != GameMode.playing) {
			ErrorLog.write("dies ist kein lesbares Objekt (Game.loadProps)");
			return;
		}
		try {
			Reader reader = new FileReader(Paths.gameProperties(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			date = prop.getProperty("date");
			duration = Integer.parseInt(prop.getProperty("duration"));
			gameType = prop.getProperty("logic");
			judged = Boolean.parseBoolean(prop.getProperty("judged"));
			
			int amount = Integer.parseInt(prop.getProperty("participantAmount"));
			participants.clear();
			for (int i = 0; i < amount; i++) {
				participants.add(new ParticipantResult(this));
				participants.get(i).playerName.set(prop.getProperty("playerName" + participants.get(i).number));
				participants.get(i).aiName.set(prop.getProperty("aiName" + participants.get(i).number));
				participants.get(i).playerID.set(Integer.parseInt(prop.getProperty("playerID" + participants.get(i).number)));
				participants.get(i).aiID.set(Integer.parseInt(prop.getProperty("aiID" + participants.get(i).number)));
				participants.get(i).duration.set(Integer.parseInt(prop.getProperty("duration" + participants.get(i).number)));
				participants.get(i).moveCount.set(Integer.parseInt(prop.getProperty("moveCount" + participants.get(i).number)));
				participants.get(i).points.set(Integer.parseInt(prop.getProperty("points" + participants.get(i).number)));
				participants.get(i).won.set(Boolean.parseBoolean(prop.getProperty("won" + participants.get(i).number)));
			}
			
		} catch (IOException e) {
			ErrorLog.write("Fehler bei Laden aus der properties.txt (Game)");
		}
	}
	
	
	public void storeProps() {
		if (mode != GameMode.playing) {
			ErrorLog.write("dies ist kein speicherbares Objekt (Game.storeProps)");
			return;
		}
		
		if (ID == -1) {
			getNewID();
		}
		
		Properties prop = new Properties();
		prop.setProperty("date", date);
		prop.setProperty("duration", duration + "");
		prop.setProperty("logic", gameType);
		prop.setProperty("judged", judged + "");
		
		prop.setProperty("participantAmount", participants.size() + "");
		for (int i = 0; i < participants.size(); i++) {
			prop.setProperty("playerName" + participants.get(i).number.get(), participants.get(i).playerName.get());
			prop.setProperty("aiName" + participants.get(i).number.get(), participants.get(i).aiName.get());
			prop.setProperty("playerID" + participants.get(i).number.get(), participants.get(i).playerID.get() + "");
			prop.setProperty("aiID" + participants.get(i).number.get(), participants.get(i).aiID.get() + "");
			prop.setProperty("duration" + participants.get(i).number.get(), participants.get(i).duration.get() + "");
			prop.setProperty("moveCount" + participants.get(i).number.get(), participants.get(i).moveCount.get() + "");
			prop.setProperty("points" + participants.get(i).number.get(), participants.get(i).points.get() + "");
			prop.setProperty("won" + participants.get(i).number.get(), participants.get(i).won.get() + "");
		}
		
		try {
			File dir = new File(Paths.game(this));
			dir.mkdirs();
			
			Writer writer = new FileWriter(Paths.gameProperties(this));
			prop.store(writer, ID + "");
			writer.close();
		} catch (IOException e) {
			ErrorLog.write("Es kann keine Properties-Datei angelegt werden. (Game)");
		}
	}
	
	
	/**
	 * Setzt den date-String auf die aktuelle Zeit
	 */
	public void setDateNow() {
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm,ss");
		date = format.format(now);
	}
	
	
	public void getNewID() {
		for (int i = 1; i < 10000; i++) {
			File dir = new File(Paths.game(i));
			if (dir.mkdirs()) {
				ID = i;
				return;
			}
		}
		ErrorLog.write("GetNewID-ERROR: Mehr als 10.000 Spielordner wurden ausprobiert: Möglicherweise gibt es keine Zugriffsberechtigung.");
	}
	
	
	public void play(List<Version> opponents) {
		try {
			game = new CodrGameImpl(this, opponents);
			CodrAiServer server = new CodrAiServer(game);
			server.start();
			
			Properties p = new Properties();
			p.put("turnierserver.worker.host", "localhost");
			p.put("turnierserver.worker.server.port", Integer.toString(server.getPort()));
			p.put("turnierserver.worker.server.aichar", "");
			p.put("turnierserver.serializer.compress.worker", "false");
			System.out.println(p);
			game.startAis(p);
		} catch (IOException | ReflectiveOperationException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < opponents.size(); i++) {
			participants.add(new ParticipantResult(this, "Lokal", 0, opponents.get(i).ai.title + "v" + opponents.get(i).number, 0, 100, 5, 20, true));
		}
		setDateNow();
		duration = 500;
	}
	
	
}

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
	
	
	public int offlineId = -1;
	public CodrGameImpl game;
	
	
	/**
	 * wird verwendet, wenn ein neues Spiel gestartet wird
	 * 
	 * @param ggameType
	 */
	public GameSaved(String ggameType) {
		super(GameMode.playing);
		gameType = ggameType;
		getNewID();
	}
	
	
	/**
	 * wird verwendet, wenn der GameManager das Spiel aus dem Dateisystem lädt.
	 * 
	 * @param iid
	 */
	public GameSaved(int iid) {
		super(GameMode.saved);
		offlineId = iid;
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
			gameType = prop.getProperty("logic");
			
		} catch (IOException e) {
			ErrorLog.write("Fehler bei Laden aus der properties.txt (Game)");
		}
	}
	
	
	public void storeProps() {
		if (mode != GameMode.playing) {
			ErrorLog.write("dies ist kein speicherbares Objekt (Game.storeProps)");
			return;
		}
		
		if (offlineId == -1) {
			getNewID();
		}
		
		Properties prop = new Properties();
		prop.setProperty("date", date);
		prop.setProperty("logic", gameType);
		
		try {
			File dir = new File(Paths.game(this));
			dir.mkdirs();
			
			Writer writer = new FileWriter(Paths.gameProperties(this));
			prop.store(writer, offlineId + "");
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
				offlineId = i;
				return;
			}
		}
		ErrorLog.write("GetNewID-ERROR: Mehr als 10.000 Spielordner wurden ausprobiert: Möglicherweise gibt es keine Zugriffsberechtigung.");
	}
	
	
	public void play(List<Version> opponents) {
		System.out.println("play(" + opponents + ")");
		try {
			game = new CodrGameImpl(this, opponents);
			System.out.println(game);
			CodrAiServer server = new CodrAiServer(game);
			System.out.println(server);
			server.start();
			System.out.println("server gestartet");
			
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
		
		setDateNow();
	}
	
	
}

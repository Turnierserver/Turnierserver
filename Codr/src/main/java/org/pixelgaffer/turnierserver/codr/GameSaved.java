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


public class GameSaved extends GameBase {


	public GameSaved(String llogic) {
		super(GameMode.playing);
		logic = llogic;
		getNewID();
	}


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
			duration = prop.getProperty("duration");
			logic = prop.getProperty("logic");
			state = prop.getProperty("state");
			judged = prop.getProperty("judged");

			int amount = Integer.parseInt(prop.getProperty("participantAmount"));
			for (int i = 0; i < amount; i++) {
				participants.get(i).playerName.set(prop.getProperty("playerName" + participants.get(i).number));
				participants.get(i).aiName.set(prop.getProperty("kiName" + participants.get(i).number));
				participants.get(i).duration.set(prop.getProperty("duration" + participants.get(i).number));
				participants.get(i).moveCount.set(prop.getProperty("moveCount" + participants.get(i).number));
				participants.get(i).points.set(prop.getProperty("points" + participants.get(i).number));
				participants.get(i).won.set(prop.getProperty("won" + participants.get(i).number));
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
		prop.setProperty("duration", duration);
		prop.setProperty("logic", logic);
		prop.setProperty("state", state);
		prop.setProperty("judged", judged);

		prop.setProperty("participantAmount", participants.size() + "");
		for (int i = 0; i < participants.size(); i++) {
			prop.setProperty("playerName" + participants.get(i).number.get(), participants.get(i).playerName.get());
			prop.setProperty("kiName" + participants.get(i).number.get(), participants.get(i).aiName.get());
			prop.setProperty("duration" + participants.get(i).number.get(), participants.get(i).duration.get());
			prop.setProperty("moveCount" + participants.get(i).number.get(), participants.get(i).moveCount.get());
			prop.setProperty("points" + participants.get(i).number.get(), participants.get(i).points.get());
			prop.setProperty("won" + participants.get(i).number.get(), participants.get(i).won.get());
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
			participants.add(new ParticipantResult(this, "Lokal", 0, opponents.get(i).ai.title + "v" + opponents.get(i).number, 0, "100ms", "5", "20", "Ja"));
		}
		setDateNow();
		duration = "500ms";
	}


}

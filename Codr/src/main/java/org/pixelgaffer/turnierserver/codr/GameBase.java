package org.pixelgaffer.turnierserver.codr;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.simulator.CodrGameImpl;


/**
 * Ist die Grund-Klasse für ein Spiel-Objekt.
 * 
 * @author Philip
 */
public class GameBase {
	
	public final GameMode mode;
	public String gameType;
	public String date;
	public ObservableList<Participant> participants = FXCollections.observableArrayList();
	
	public class Participant {
		public String name = "";
		public int id = -1;
		
		public Participant(JSONObject json) {
			name = json.getString("name");
			id = json.getInt("id");
		}
		public Participant() {}
	}
	
	
	public static enum GameMode {
		playing, saved, onlineLoaded, onlineInprogress
	}
	
	
	protected GameBase(GameMode mmode) {
		mode = mmode;
	}
	
	
}

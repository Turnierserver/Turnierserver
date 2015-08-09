package org.pixelgaffer.turnierserver.codr;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.pixelgaffer.turnierserver.codr.simulator.CodrGameImpl;


/**
 * Ist die Grund-Klasse für ein Spiel-Objekt.
 * 
 * @author Philip
 */
public class GameBase {
	
	public final GameMode mode;
	public int ID = -1;
	public String date;
	public int duration;
	public String gameType;
	public boolean judged;
	public CodrGameImpl game;
	public ObservableList<ParticipantResult> participants = FXCollections.observableArrayList();
	
	
	public static enum GameMode {
		playing, saved, onlineLoaded
	}
	
	
	protected GameBase(GameMode mmode) {
		mode = mmode;
	}
	
	
}

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
	public String gameType;
	public String date;
	
	
	public static enum GameMode {
		playing, saved, onlineLoaded, onlineInprogress
	}
	
	
	protected GameBase(GameMode mmode) {
		mode = mmode;
	}
	
	
}

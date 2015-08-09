package org.pixelgaffer.turnierserver.codr;


import java.io.File;

import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Managet das Laden der Spiele aus dem Dateisystem.
 * 
 * @author Philip
 */
public class GameManager {
	
	public ObservableList<GameBase> games = FXCollections.observableArrayList();
	
	
	/**
	 * Lädt alle Spieler aus dem Dateisystem in die Liste
	 */
	public void loadGames() {
		games.clear();
		File dir = new File(Paths.gameFolder());
		dir.mkdirs();
		File[] dirs = dir.listFiles();
		if (dirs == null) {
			ErrorLog.write("keine Spieler vorhanden");
			return;
		}
		for (int i = 0; i < dirs.length; i++) {
			if (dirs[i].isDirectory())
				games.add(new GameSaved(Integer.parseInt(dirs[i].getName())));
		}
		
	}
}

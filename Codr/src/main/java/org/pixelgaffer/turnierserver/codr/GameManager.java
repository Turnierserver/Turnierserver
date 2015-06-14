package org.pixelgaffer.turnierserver.codr;


import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.pixelgaffer.turnierserver.codr.CodrGame.GameMode;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;



public class GameManager {
	
	public ObservableList<CodrGame> games = FXCollections.observableArrayList();
	
	
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
				games.add(new CodrGame(dirs[i].getName(), GameMode.saved));
		}
		
	}
}

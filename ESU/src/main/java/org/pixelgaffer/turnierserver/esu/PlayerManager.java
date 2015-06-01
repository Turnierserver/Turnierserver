package org.pixelgaffer.turnierserver.esu;

import java.io.File;

import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Paths;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayerManager {
	

	public ObservableList<Player> players = FXCollections.observableArrayList();
	
	/**
	 * Lädt alle Spieler aus dem Dateisystem in die Liste
	 */
	public void loadPlayers(){
		players.clear();
		File dir = new File(Paths.playerFolder());
		dir.mkdirs();
		File[] playerDirs = dir.listFiles();
		if (playerDirs == null){
			ErrorLog.write("keine Spieler vorhanden");
			return;
		}
		for (int i = 0; i < playerDirs.length; i++){
			players.add(new Player(playerDirs[i].getName()));
		}
	}
	
	
}

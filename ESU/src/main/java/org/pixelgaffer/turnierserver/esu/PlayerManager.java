package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.pixelgaffer.turnierserver.esu.Player.PlayerMode;
import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Paths;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

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
			players.add(new Player(playerDirs[i].getName(), PlayerMode.saved));
		}

		File simpleDir = new File(Paths.simplePlayerFolder());
		simpleDir.mkdirs();
		File[] simpleDirs = simpleDir.listFiles();
		if (simpleDirs == null){
			ErrorLog.write("keine SimplePlayer vorhanden");
			return;
		}
		for (int i = 0; i < simpleDirs.length; i++){
			players.add(new Player(simpleDirs[i].getName(), PlayerMode.simplePlayer));
		}
	}
	
	
}

package org.pixelgaffer.turnierserver.esu;

import java.io.File;

import org.pixelgaffer.turnierserver.esu.utilities.*;

import javafx.collections.*;

public class GameManager {

	public ObservableList<Game> games = FXCollections.observableArrayList();
	
	/**
	 * Lädt alle Spieler aus dem Dateisystem in die Liste
	 */
	public void loadPlayers(){
		games.clear();
		File dir = new File(Resources.gameFolder());
		dir.mkdirs();
		File[] gameDirs = dir.listFiles();
		if (gameDirs == null){
			ErrorLog.write("keine Spieler vorhanden");
			return;
		}
		for (int i = 0; i < gameDirs.length; i++){
			games.add(new Game(gameDirs[i].getName()));
		}
	}
}

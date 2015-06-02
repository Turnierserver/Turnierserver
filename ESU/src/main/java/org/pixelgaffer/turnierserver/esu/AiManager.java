package org.pixelgaffer.turnierserver.esu;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.pixelgaffer.turnierserver.esu.Ai.AiMode;
import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Paths;

public class AiManager {
	

	public ObservableList<Ai> ais = FXCollections.observableArrayList();
	
	/**
	 * Lädt alle Spieler aus dem Dateisystem in die Liste
	 */
	public void loadPlayers(){
		ais.clear();
		File dir = new File(Paths.aiFolder());
		dir.mkdirs();
		File[] playerDirs = dir.listFiles();
		if (playerDirs == null){
			ErrorLog.write("keine Spieler vorhanden");
			return;
		}
		for (int i = 0; i < playerDirs.length; i++){
			ais.add(new Ai(playerDirs[i].getName(), AiMode.saved));
		}

		File simpleDir = new File(Paths.simplePlayerFolder(MainApp.actualGameType));
		simpleDir.mkdirs();
		File[] simpleDirs = simpleDir.listFiles();
		if (simpleDirs == null){
			ErrorLog.write("keine SimplePlayer vorhanden");
			return;
		}
		for (int i = 0; i < simpleDirs.length; i++){
			ais.add(new Ai(simpleDirs[i].getName(), AiMode.simplePlayer));
		}
	}
	
	
}

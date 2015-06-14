package org.pixelgaffer.turnierserver.codr;


import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;



public class AiManager {
	
	
	public ObservableList<AiSimple> ais = FXCollections.observableArrayList();
	
	
	/**
	 * Lädt alle Spieler aus dem Dateisystem in die Liste
	 */
	public void loadAis() {
		ais.clear();
		File dir = new File(Paths.aiFolder());
		dir.mkdirs();
		File[] playerDirs = dir.listFiles();
		if (playerDirs == null) {
			ErrorLog.write("keine Spieler vorhanden");
			return;
		}
		for (int i = 0; i < playerDirs.length; i++) {
			if (playerDirs[i].isDirectory())
				ais.add(new AiSaved(playerDirs[i].getName()));
		}
		
		File simpleDir = new File(Paths.simplePlayerFolder(MainApp.actualGameType.get()));
		simpleDir.mkdirs();
		File[] simpleDirs = simpleDir.listFiles();
		if (simpleDirs == null) {
			ErrorLog.write("keine SimplePlayer vorhanden");
			return;
		}
		for (int i = 0; i < simpleDirs.length; i++) {
			if (simpleDirs[i].isDirectory())
				ais.add(new AiSimple(simpleDirs[i].getName()));
		}
	}
	
	
}

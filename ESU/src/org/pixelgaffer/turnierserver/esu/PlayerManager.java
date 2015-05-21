package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayerManager {
	

	public ObservableList<Player> players = FXCollections.observableArrayList();
	
	
	public void loadPlayers(){
		players.clear();
		File dir = new File(Resources.playerFolder());
		File[] playerDirs = dir.listFiles();
		for (int i = 0; i < playerDirs.length; i++){
			players.add(new Player(playerDirs[i].getName()));
		}
	}
	
	
}

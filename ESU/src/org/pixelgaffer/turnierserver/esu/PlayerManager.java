package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
	

	List<Player> players = new ArrayList<Player>();
	
	
	public void loadPlayers(){
		players.clear();
		File dir = new File(Paths.playerFolder());
		File[] playerDirs = dir.listFiles();
		for (int i = 0; i < playerDirs.length; i++){
			players.add(new Player(playerDirs[i].getName()));
		}
	}
	
	
}

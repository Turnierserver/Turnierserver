/*
 * GameManager.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	
	public ObservableList<GameSaved> games = FXCollections.observableArrayList();
	
	
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

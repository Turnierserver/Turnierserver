/*
 * GameBase.java
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


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.simulator.CodrGameImpl;


/**
 * Ist die Grund-Klasse für ein Spiel-Objekt.
 * 
 * @author Philip
 */
public class GameBase {
	
	public final GameMode mode;
	public String gameType;
	public String date;
	public ObservableList<Participant> participants = FXCollections.observableArrayList();
	
	public class Participant {
		public String name = "";
		public int id = -1;
		
		public Participant(JSONObject json) {
			name = json.getString("name");
			id = json.getInt("id");
		}
		public Participant() {}
		
		public Participant(AiOnline ai) {
			name = ai.title;
			id = ai.id;
		}
	}
	
	
	public static enum GameMode {
		playing, saved, onlineLoaded, onlineInprogress
	}
	
	
	protected GameBase(GameMode mmode) {
		mode = mmode;
	}
	
	
}

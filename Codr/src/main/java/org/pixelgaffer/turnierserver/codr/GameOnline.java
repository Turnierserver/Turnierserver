/*
 * GameOnline.java
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


import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.utilities.WebConnector;


/**
 * Stellt ein Online-Spiel bereit, das auf dem Turnierserver existiert.
 * Dieses Spiel wird nicht lokal gespeichert.
 * 
 * @author Nico
 */
public class GameOnline extends GameBase {
	
	
	public int onlineId;
	
	
	
	/**
	 * Konstruktor, mit dem das Spiel aus dem WebConnector geladen wird
	 * 
	 * @param json das JSON-Objekt, in dem alle Informationen über das Spiel gespeichert sind.
	 * @param connector eine Referenz auf den WebConnector, mit dem Informationen über das Spiel nachgeladen werden können.
	 */
	public GameOnline(JSONObject json, WebConnector connector) {
		super(GameMode.onlineLoaded);
		
		onlineId = json.getInt("id");
		JSONObject gametypeObject = json.getJSONObject("type");
		gameType = gametypeObject.getString("name");
		date = json.getString("timestr");
		JSONArray array = json.getJSONArray("ais");
		for(int i = 0; i < array.length(); i++) {
			participants.add(new Participant(array.getJSONObject(i)));
		}
	}
	
	public GameOnline(int tempId, AiOnline...ais) {
		super(GameMode.onlineInprogress);
		onlineId = tempId;
		date = "Gerade eben";
		for(AiOnline ai : ais) {
			participants.add(new Participant(ai));
		}
	}
	
	
}

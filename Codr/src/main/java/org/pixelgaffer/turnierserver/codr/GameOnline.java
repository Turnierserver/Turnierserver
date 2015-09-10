package org.pixelgaffer.turnierserver.codr;


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
		date = gametypeObject.getString("timestampstr");
		JSONArray array = json.getJSONArray("ais");
		for(int i = 0; i < array.length(); i++) {
			participants.add(new Participant(array.getJSONObject(i)));
		}
	}
	
	public GameOnline(int tempId) {
		super(GameMode.onlineInprogress);
		onlineId = tempId;
	}
	
	
}

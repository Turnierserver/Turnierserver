package org.pixelgaffer.turnierserver.codr;


import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.utilities.WebConnector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Stellt ein Online-Spiel bereit, das auf dem Turnierserver existiert.
 * Dieses Spiel wird nicht lokal gespeichert.
 * 
 * @author Nico
 */
public class GameOnline extends GameBase {
	
	
	private ObservableList<AiOnline> ais = FXCollections.observableArrayList();
	private int id;
	
	
	/**
	 * Konstruktor, mit dem das Spiel aus dem WebConnector geladen wird
	 * 
	 * @param json das JSON-Objekt, in dem alle Informationen über das Spiel gespeichert sind.
	 * @param connector eine Referenz auf den WebConnector, mit dem Informationen über das Spiel nachgeladen werden können.
	 */
	public GameOnline(JSONObject json, WebConnector connector) {
		super(GameMode.onlineLoaded);
		
		id = json.getInt("id");
		super.ID = id;
		JSONObject gametypeObject = json.getJSONObject("type");
		gameType = gametypeObject.getString("name");
		JSONArray aiArray = json.getJSONArray("ais");
		for (int i = 0; i < aiArray.length(); i++) {
			JSONObject aiObject = aiArray.getJSONObject(i);
			ParticipantResult part = new ParticipantResult(this);
			part.playerID.set(aiObject.getInt("author_id"));
			part.playerName.set(aiObject.getString("author"));
			part.aiName.set(aiObject.getString("name"));
			part.aiID.set(aiObject.getInt("id"));
			participants.add(part);
		}
	}
	
	
}

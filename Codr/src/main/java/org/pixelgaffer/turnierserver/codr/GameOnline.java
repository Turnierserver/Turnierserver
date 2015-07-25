package org.pixelgaffer.turnierserver.codr;


import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.utilities.WebConnector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



public class GameOnline extends GameBase {
	
	
	private ObservableList<AiOnline> ais = FXCollections.observableArrayList();
	private int id;
	private String gameType;  //TODO
	private int gameTypeId;
	
	public GameOnline(JSONObject json, WebConnector connector) {
		super(GameMode.onlineLoaded);
		
		id = json.getInt("id");
		super.ID = id;
		JSONObject gametypeObject = json.getJSONObject("type");
		gameType = gametypeObject.getString("name");
		gameTypeId = gametypeObject.getInt("id");
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

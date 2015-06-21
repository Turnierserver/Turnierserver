package org.pixelgaffer.turnierserver.codr;


import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.utilities.WebConnector;



public class GameOnline extends GameBase {
	
	private List<AiOnline> ais;
	private int id;
	private String gameType;
	private int gameTypeId;
	
	public GameOnline(JSONObject json, WebConnector connector) {
		super(GameMode.onlineLoaded);
		id = json.getInt("id");
		JSONObject gametypeObject = json.getJSONObject("type");
		gameType = gametypeObject.getString("name");
		gameTypeId = gametypeObject.getInt("id");
		JSONArray aiArray = json.getJSONArray("ais");
		for(int i = 0; i < aiArray.length(); i++) {
			JSONObject aiObject = aiArray.getJSONObject(i);
			ais.add(new AiOnline(json, connector));
		}
	}
	
	
	
}

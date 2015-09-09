package org.pixelgaffer.turnierserver.codr;

import org.json.JSONObject;

public class OnlineGameInfo {
	public int id = -1;
	public String date = "Morgen";
	public String enemy = "Fritz";
	public boolean won = true;

	public OnlineGameInfo() {

	}

	public OnlineGameInfo(JSONObject json, int aiId) {
		id = json.getInt("id");
		date = json.getString("timestr");
		int aiPos = json.getJSONArray("ai").getJSONObject(0).getInt("id") == aiId ? 0 : 1;
		enemy = json.getJSONArray("ai").getJSONObject(1 - aiPos).getString("name");
		won = json.getJSONArray("scores").getInt(aiPos) > json.getJSONArray("scores").getInt(1 - aiPos);
	}
	
	public OnlineGameInfo(int iid, String ddate, String eenemy, boolean wwon) {
		id = iid;
		date = ddate;
		enemy = eenemy;
		won = wwon;
	}
}

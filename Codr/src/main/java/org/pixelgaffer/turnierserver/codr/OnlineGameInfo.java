/*
 * OnlineGameInfo.java
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
		int aiPos = json.getJSONArray("ais").getJSONObject(0).getInt("id") == aiId ? 0 : 1;
		enemy = json.getJSONArray("ais").getJSONObject(1 - aiPos).getString("name");
		won = json.getJSONObject("scores").getInt(json.getJSONArray("ais").getJSONObject(aiPos).getInt("id") + "") > json.getJSONObject("scores").getInt(json.getJSONArray("ais").getJSONObject(1-aiPos).getInt("id") + "");
	}
	
	public OnlineGameInfo(int iid, String ddate, String eenemy, boolean wwon) {
		id = iid;
		date = ddate;
		enemy = eenemy;
		won = wwon;
	}
}

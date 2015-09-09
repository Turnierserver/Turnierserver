package org.pixelgaffer.turnierserver.codr;

public class OnlineGameInfo {
	public int id = -1;
	public String date = "Morgen";
	public String enemy = "Fritz";
	public boolean won = true;

	public OnlineGameInfo() {

	}

	public OnlineGameInfo(int iid, String ddate, String eenemy, boolean wwon) {
		id = iid;
		date = ddate;
		enemy = eenemy;
		won = wwon;
	}
}

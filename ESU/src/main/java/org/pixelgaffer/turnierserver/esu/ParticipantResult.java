package org.pixelgaffer.turnierserver.esu;

public class ParticipantResult {
	
	public String playerName;
	public String kiName;
	public String duration;
	public String moveCount;
	public String points;
	public String won;
	
	public ParticipantResult(String pplayerName, String kkiName, String dduration, String mmoveCount, String ppoints, String wwon){
		playerName = pplayerName;
		kiName = kkiName;
		duration = dduration;
		moveCount = mmoveCount;
		points = ppoints;
		won = wwon;
	}
	
	
	
}

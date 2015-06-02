package org.pixelgaffer.turnierserver.esu;

import javafx.beans.property.SimpleStringProperty;

public class ParticipantResult {
	
	public final Game game;
	public SimpleStringProperty number;
	public SimpleStringProperty playerName;
	public SimpleStringProperty kiName;
	public SimpleStringProperty duration;
	public SimpleStringProperty moveCount;
	public SimpleStringProperty points;
	public SimpleStringProperty won;
	
	public ParticipantResult(Game ggame, String pplayerName, String kkiName, String dduration, String mmoveCount, String ppoints, String wwon){
		game = ggame;
		playerName.set(pplayerName);
		kiName.set(kkiName);
		duration.set(dduration);
		moveCount.set(mmoveCount);
		points.set(ppoints);
		won.set(wwon);
	}
	
	
	
	
}

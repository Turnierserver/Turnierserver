package org.pixelgaffer.turnierserver.codr;


import javafx.beans.property.SimpleStringProperty;



public class ParticipantResult {
	
	public final GameBase game;
	public SimpleStringProperty number = new SimpleStringProperty();
	public SimpleStringProperty playerName = new SimpleStringProperty();
	public SimpleStringProperty kiName = new SimpleStringProperty();
	public SimpleStringProperty duration = new SimpleStringProperty();
	public SimpleStringProperty moveCount = new SimpleStringProperty();
	public SimpleStringProperty points = new SimpleStringProperty();
	public SimpleStringProperty won = new SimpleStringProperty();
	
	
	public ParticipantResult(GameBase ggame, String pplayerName, String kkiName, String dduration, String mmoveCount, String ppoints, String wwon) {
		game = ggame;
		playerName.set(pplayerName);
		kiName.set(kkiName);
		duration.set(dduration);
		moveCount.set(mmoveCount);
		points.set(ppoints);
		won.set(wwon);
	}
	
	
}

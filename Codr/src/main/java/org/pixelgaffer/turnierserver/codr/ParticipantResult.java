package org.pixelgaffer.turnierserver.codr;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class ParticipantResult {

	public final GameBase game;
	public SimpleStringProperty number = new SimpleStringProperty();
	public SimpleStringProperty playerName = new SimpleStringProperty();
	public SimpleIntegerProperty playerID = new SimpleIntegerProperty();
	public AiBase ai;
	public SimpleStringProperty aiName = new SimpleStringProperty();
	public SimpleIntegerProperty aiID = new SimpleIntegerProperty();
	public SimpleStringProperty duration = new SimpleStringProperty();
	public SimpleStringProperty moveCount = new SimpleStringProperty();
	public SimpleStringProperty points = new SimpleStringProperty();
	public SimpleStringProperty won = new SimpleStringProperty();


	public ParticipantResult(GameBase ggame) {
		game = ggame;
	}


	public ParticipantResult(GameBase ggame, String pplayerName, int pplayerID, String aaiName, int aaiID, String dduration, String mmoveCount, String ppoints, String wwon) {
		game = ggame;
		playerName.set(pplayerName);
		playerID.set(pplayerID);
		aiName.set(aaiName);
		aiID.set(aaiID);
		duration.set(dduration);
		moveCount.set(mmoveCount);
		points.set(ppoints);
		won.set(wwon);
	}


}

package org.pixelgaffer.turnierserver.codr;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class ParticipantResult {

	public final GameBase game;
	public SimpleIntegerProperty number = new SimpleIntegerProperty();
	public SimpleStringProperty playerName = new SimpleStringProperty();
	public SimpleIntegerProperty playerID = new SimpleIntegerProperty();
	public AiBase ai;
	public SimpleStringProperty aiName = new SimpleStringProperty();
	public SimpleIntegerProperty aiID = new SimpleIntegerProperty();
	public SimpleIntegerProperty duration = new SimpleIntegerProperty();
	public SimpleIntegerProperty moveCount = new SimpleIntegerProperty();
	public SimpleIntegerProperty points = new SimpleIntegerProperty();
	public SimpleBooleanProperty won = new SimpleBooleanProperty();


	public ParticipantResult(GameBase ggame) {
		game = ggame;
	}


	public ParticipantResult(GameBase ggame, String pplayerName, int pplayerID, String aaiName, int aaiID, int dduration, int mmoveCount, int ppoints, boolean wwon) {
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

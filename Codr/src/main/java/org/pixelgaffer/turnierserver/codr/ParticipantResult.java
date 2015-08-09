package org.pixelgaffer.turnierserver.codr;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


/**
 * Dient nur zur Speicherung der Spielerergebnisse bei einem Spiel.
 * 
 * @author Philip
 */
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
	
	
	/**
	 * Wird verwendet, wenn die Eigenschaften einzeln per JSON oder aus dem Dateisystem geladen werden.
	 * 
	 * @param ggame das übergeordnete Spiel
	 */
	public ParticipantResult(GameBase ggame) {
		game = ggame;
	}
	
	
	/**
	 * Wird bei einem neuen Spielstart erstellt.
	 */
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

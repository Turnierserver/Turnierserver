package org.pixelgaffer.turnierserver.gamelogic.messages;

import java.util.Map;
import java.util.UUID;

public class GameFinished {
	
	/**
	 * Die Scores der AIs
	 */
	public Map<String, Integer> scores;
	/**
	 * Die Position der AIs, sortiert nach Score
	 */
	public Map<String, Integer> position;
	/**
	 * Die übrigen Rechenpunkte
	 */
	public Map<String, Integer> leftoverMillis;
	/**
	 * Die requestid des Frontendauftrages
	 */
	public int requestid;
	/**
	 * Der Grund, warum das Spiel beendet wurde
	 */
	public String reason;
	/**
	 * Die ID des Spieles in einem Turnier
	 */
	public UUID gameId;
	
}

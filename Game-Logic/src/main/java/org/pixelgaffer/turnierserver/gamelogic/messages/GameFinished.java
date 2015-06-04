package org.pixelgaffer.turnierserver.gamelogic.messages;

import java.util.Map;

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
}

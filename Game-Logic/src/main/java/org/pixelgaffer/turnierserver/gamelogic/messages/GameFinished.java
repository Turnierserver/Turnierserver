package org.pixelgaffer.turnierserver.gamelogic.messages;

import java.util.Map;

public class GameFinished {
	
	/**
	 * Die Scores der AIs
	 */
	public Map<Integer, Integer> scores;
	/**
	 * Die Position der AIs, sortiert nach Score
	 */
	public Map<Integer, Integer> position;
	/**
	 * Die übrigen Rechenpunkte
	 */
	public Map<Integer, Integer> leftoverMillis;
	
}

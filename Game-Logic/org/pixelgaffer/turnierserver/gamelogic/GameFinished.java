package org.pixelgaffer.turnierserver.gamelogic;

import org.msgpack.annotation.Message;

@Message
public class GameFinished {
	
	/**
	 * Die Scores der AIs, soriert nach IDs
	 */
	public int[] scores;
	/**
	 * Ob die AIs gewonnen haben oder nicht, sortiert nach IDs
	 */
	public boolean[] won;
	/**
	 * Die übrigen Rechenpunkte
	 */
	public int[] leftoverMillis;
	
}

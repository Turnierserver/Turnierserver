package org.pixelgaffer.turnierserver.gamelogic;

import org.msgpack.annotation.Message;

@Message
public class GameFinished {
	
	/**
	 * Die Scores der AIs, soriert nach IDs
	 */
	public int[] scores;
	/**
	 * Die übrigen Rechenpunkte
	 */
	public int[] leftoverMillis;
	
}

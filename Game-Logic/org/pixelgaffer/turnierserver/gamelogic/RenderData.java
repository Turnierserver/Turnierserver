package org.pixelgaffer.turnierserver.gamelogic;

import org.msgpack.annotation.Message;

@Message
public class RenderData {
	
	public String type = "RenderData";
	/**
	 * Die ID des Spieles
	 */
	public int gameId;
	/**
	 * Die Nummer des updates
	 */
	public int update;
	/**
	 * Die Daten, die an den renderer gesendet werden sollen
	 */
	public Object data;
	
}

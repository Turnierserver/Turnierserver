package org.pixelgaffer.turnierserver.gamelogic.messages;

public class RenderData {
	
	/**
	 * Die ID des Spieles
	 */
	public int gameId;
	/**
	 * Die Nummer des updates
	 */
	public int update;
	/**
	 * Die Fsortschrittsanzeige des Spieles
	 */
	public String display;
	/**
	 * Die Daten, die an den renderer gesendet werden sollen
	 */
	public Object data;
	/**
	 * Die requestid des Frontendauftrages
	 */
	public int requestid;
	
}

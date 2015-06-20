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
	 * Die Fortschrittsanzeige des Spieles
	 */
	public String display = "Spiel ist noch am laufen...";
	/**
	 * Die Daten, die an den renderer gesendet werden sollen
	 */
	public Object data;
	/**
	 * Die requestid des Frontendauftrages
	 */
	public int requestid;
	
}

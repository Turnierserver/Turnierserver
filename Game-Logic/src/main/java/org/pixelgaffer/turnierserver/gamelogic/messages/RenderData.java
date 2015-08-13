package org.pixelgaffer.turnierserver.gamelogic.messages;

import java.util.HashMap;

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
	/**
	 * Der Fortschritt des Spieles zwischen 0 und 1, -1 wenn kein Fortschritt angezeigt werden soll
	 */
	public double progress = -1;
	/**
	 * Die übrigen Rechenpunkte der AIs
	 */
	public HashMap<String, Integer> calculationPoints;
	/**
	 * Die Punkte der AIs
	 */
	public HashMap<String, Integer> points;
}

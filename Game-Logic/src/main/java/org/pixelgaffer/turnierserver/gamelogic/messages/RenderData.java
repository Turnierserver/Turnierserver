/*
 * RenderData.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	public HashMap<String, Float> calculationPoints;
	/**
	 * Die Punkte der AIs
	 */
	public HashMap<String, Integer> points;
}

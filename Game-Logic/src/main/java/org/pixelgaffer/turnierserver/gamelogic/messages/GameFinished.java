/*
 * GameFinished.java
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
	/**
	 * Der Grund, warum das Spiel beendet wurde
	 */
	public String reason;
	
}

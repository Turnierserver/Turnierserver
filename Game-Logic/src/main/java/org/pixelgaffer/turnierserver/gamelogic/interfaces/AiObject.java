/*
 * AiObject.java
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
package org.pixelgaffer.turnierserver.gamelogic.interfaces;

import java.io.IOException;

import org.pixelgaffer.turnierserver.Logger;
import org.pixelgaffer.turnierserver.gamelogic.GameLogic;
import org.pixelgaffer.turnierserver.gamelogic.messages.LostMessage;

import lombok.Setter;

public class AiObject {
	
	static final Logger logger = new Logger();
	
	/**
	 * True, wenn die AI schon verloren hat (z.B. wenn sie aufgegeben hat)
	 */
	public boolean lost = false;
	
	/**
	 * Die übrigen Rechenpunkte dieser AI
	 */
	public int mikrosLeft;
	
	/**
	 * Der score der AI, der am Ende in die Datenbank gespeichert wird (z.B. +1 für den Sieger, -1 für den Verlierer)
	 */
	public int score = 0;
	
	@Setter
	private GameLogic<?, ?> logic;
	@Setter
	private Ai ai;
	
	public boolean subtractMikros(int mikros) {
		mikrosLeft -= mikros;
		if(mikros < 0 && !lost) {
			loose("Die Ki hatte keine Zeit mehr");
		}
		return mikros < 0;
	}
	
	public void loose(String reason) {
		try {
			ai.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logic.sendToFronted(new LostMessage(reason, ai.getId(), logic.getGame().getFrontend().getRequestId()));
		lost = true;
		logic.lost(ai);
	}
	
}

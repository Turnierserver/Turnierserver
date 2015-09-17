package org.pixelgaffer.turnierserver.gamelogic.interfaces;

import java.io.IOException;

import org.pixelgaffer.turnierserver.Airbrake;
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
		if(mikrosLeft <= 0 && !lost) {
			loose("Die Ki hatte keine Zeit mehr");
		}
		return mikrosLeft < 0;
	}
	
	public void loose(String reason) {
		try {
			ai.disconnect();
		} catch (IOException e) {
			Airbrake.log(e).printStackTrace();
		}
		logic.sendToFronted(new LostMessage(reason, ai.getId(), logic.getGame().getFrontend().getRequestId()));
		lost = true;
		logic.lost(ai);
	}
	
}

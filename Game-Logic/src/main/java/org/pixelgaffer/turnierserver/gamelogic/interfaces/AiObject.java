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
	public int millisLeft;
	
	/**
	 * Der score der AI, der am Ende in die Datenbank gespeichert wird (z.B. +1 für den Sieger, -1 für den Verlierer)
	 */
	public int score = 0;
	
	@Setter
	private GameLogic<?, ?> logic;
	@Setter
	private Ai ai;
	
	/**
	 * Gibt an, ob geupdated werden soll
	 */
	private boolean updating = false;
	
	/**
	 * Das letzte mal als startCalculationTimer oder updateCalculationTimer aufgerufen wurde
	 */
	private long lastCalculationStart = -1;
	
	/**
	 * Startet den Berechnungtimer
	 */
	public void startCalculationTimer() {
		startCalculationTimer(-1);
	}
	
	/**
	 * Startet den Berechnungtimer
	 * 
	 * @param Die
	 *            Intervalle, in welchen die Rechenpunkte geupdated werden sollen (<= 0, wenn sie nicht automatisch geupdated werden sollen)
	 */
	public void startCalculationTimer(final int updateTime) {
		if (lastCalculationStart != -1) {
			return;
		}
		lastCalculationStart = System.currentTimeMillis();
		if (updateTime > 0) {
			updating = true;
			new Thread(() -> {
				while (updating) {
					updateCalculationTimer();
					try {
						Thread.sleep(updateTime);
					} catch (Exception e) {
						return;
					}
				}
			}).start();
		}
	}
	
	/**
	 * Hält den Berechnungstimer an
	 */
	public boolean stopCalculationTimer() {
		if (lastCalculationStart == -1) {
			return lost;
		}
		if (updating) {
			updating = false;
		}
		millisLeft -= System.currentTimeMillis() - lastCalculationStart;
		lastCalculationStart = -1;
		if (millisLeft <= 0) {
			loose("Die KI hat keine Zeit mehr");
		}
		return lost;
	}
	
	/**
	 * Hält den Berechnungstimer an
	 */
	public void stop() {
		if (updating) {
			updating = false;
		}
	}
	
	/**
	 * Updated die übrige Berechnungszeit
	 */
	public void updateCalculationTimer() {
		if (lastCalculationStart == -1) {
			return;
		}
		long currentMillis = System.currentTimeMillis();
		millisLeft -= currentMillis - lastCalculationStart;
		lastCalculationStart = currentMillis;
		if (millisLeft <= 0) {
			stopCalculationTimer();
		}
	}
	
	public void loose(String reason) {
		try {
			ai.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
		logic.sendToFronted(new LostMessage(reason, ai.getId(), logic.getGame().getFrontend().getRequestId()));
		lost = true;
		logic.lost(ai);
	}
	
}

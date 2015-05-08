package org.pixelgaffer.turnierserver.gamelogic;

import lombok.Setter;

import org.pixelgaffer.turnierserver.backend.AiWrapper;

public class AiObject {
	
	/**
	 * True, wenn die AI schon verloren hat (z.B. wenn sie aufgegeben hat)
	 */
	public boolean lost = false;
	
	/**
	 * DIe übrigen Rechenpunkte dieser AI
	 */
	public int millisLeft;
	
	@Setter
	private GameLogic<?, ?> logic;
	@Setter
	private AiWrapper ai;
	
	/**
	 * Gibt an, ob geupdated werden soll
	 */
	private boolean updating = false;
	
	/**
	 * Das letzte mal als startCalculationTimer oder updateCalculationTimer aufgerufen wurde
	 */
	private long lastCalculationStart;
	
	/**
	 * Startet den Berechnungtimer
	 */
	public void startCalculationTimer() {
		startCalculationTimer(-1);
	}
	
	/**
	 * Startet den Berechnungtimer
	 * 
	 * @param Die Intervalle, in welchen die Rechenpunkte geupdated werden sollen (<= 0, wenn sie nicht automatisch geupdated werden sollen)
	 */ 
	public void startCalculationTimer(final int updateTime) {
		if(lastCalculationStart != -1) {
			return;
		}
		lastCalculationStart = System.currentTimeMillis();
		if(updateTime > 0) {
			updating = true;
			new Thread(() -> {
				while(updating) {
					try {
						Thread.sleep(updateTime);
					} catch (Exception e) {
						return;
					}
					updateCalculationTimer();
				}
			}).start();
		}
	}
	
	/**
	 * Hält den Berechnungstimer an
	 */
	public void stopCalculationTimer() {
		if(lastCalculationStart == -1) {
			return;
		}
		if(updating) {
			updating = false;
		}
		millisLeft -= System.currentTimeMillis()  - lastCalculationStart;
		lastCalculationStart = -1;
		if(millisLeft <= 0) {
			lost = true;
			logic.lost(ai);
		}
	}
	
	/**
	 * Updated die übrige Berechnungszeit
	 */
	public void updateCalculationTimer() {
		if(lastCalculationStart == -1) {
			return;
		}
		millisLeft -= System.currentTimeMillis()  - lastCalculationStart;
		lastCalculationStart = System.currentTimeMillis();
		if(millisLeft <= 0) {
			lost = true;
		}
	}
	
}

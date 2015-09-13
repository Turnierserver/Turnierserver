/*
 * GameState.java
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

/**
 * @param <C>
 *            Das Veränderungsobjekt, welches an die Ai gesendet werden soll
 * @param <R>
 *            Die Antwort der Ai
 */
public interface GameState<C, R> {
	
	/**
	 * Gibt ein Objekt mit allen Änderungen dieses Gamestates zurück, welche an eine bestimme Ai gesendet werden sollen.
	 * 
	 * @param Die Ai, an welche die Veränderungen gesendet werden sollen
	 * @return Die Änderungen, welche an diesem Gamestate vollzogen wurden
	 */
	public C getChanges(Ai ai);
	
	/**
	 * Leert alle Veränderungen für eine Ai
	 * 
	 * @param ai Die Ai, für welche die Veränderungen geleert werden sollen
	 */
	public void clearChanges(Ai ai);
	
	/**
	 * Führt Änderungen durch, welche eine Ai vorgenommen hat. Wird nur auf der GameLogic aufgerufen.
	 * 
	 * @param response Die Antwort des Clients, welche den GameState verändern soll
	 * @param ai Die Ai, welche geantwortet hat
	 */
	public void applyChanges(R response, Ai ai);
	
	/**
	 * Führt Änderungen durch, welche von der GameLogic vorgenommen wurden. Wird nur auf der Ai aufgerufen.
	 * 
	 * @param changes
	 */
	public void applyChanges(C changes);
	
}

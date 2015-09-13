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

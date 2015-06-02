package org.pixelgaffer.turnierserver.gamelogic.interfaces;

import java.io.IOException;

public interface Ai
{
	/**
	 * Gibt den Index in der Ai-List zurück
	 * 
	 * @return Der Index dieser Ai in der Ai-List
	 */
	public int getIndex ();
	
	/**
	 * Gibt die ID der Ai zurück
	 * 
	 * @return Die ID der Ai
	 */
	public int getId ();
	
	/**
	 * Sendet eine Nachricht an die Ai
	 * 
	 * @param message Die Nachricht
	 */
	public void sendMessage (byte[] message) throws IOException;
	
	/**
	 * Schließt die Verbindung zur AI. Falls die Verbindung schon geschlossen
	 * wurde, wird nichts gemacht. Ist da, um Worker-Kapazitäten freizugeben.
	 * 
	 * @throws IOException
	 */
	public void disconnect () throws IOException;
	
	/**
	 * Gibt das AiObject dieser Ai zurück
	 * 
	 * @return Das AiObject dieser Ai
	 */
	public AiObject getObject ();
	
	/**
	 * Setzt das AiObject dieser Ai
	 * 
	 * @param object Das AiObject
	 */
	public void setObject (AiObject object);
	
}

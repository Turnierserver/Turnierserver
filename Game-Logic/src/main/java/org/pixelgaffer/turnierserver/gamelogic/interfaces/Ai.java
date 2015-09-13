/*
 * Ai.java
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

public interface Ai {
	
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
	public String getId ();
	
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

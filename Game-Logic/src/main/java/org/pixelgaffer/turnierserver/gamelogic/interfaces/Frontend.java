package org.pixelgaffer.turnierserver.gamelogic.interfaces;

import java.io.IOException;

public interface Frontend {
	
	/**
	 * Gibt die RequestId des Auftrags zurück.
	 */
	public int getRequestId ();
	
	/**
	 * Sendet eine Nachricht an das Frontend
	 * 
	 * @param message Die Nachricht
	 */
	public void sendMessage (byte[] message) throws IOException;
	
}

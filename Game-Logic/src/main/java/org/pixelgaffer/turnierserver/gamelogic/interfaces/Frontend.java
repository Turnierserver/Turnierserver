package org.pixelgaffer.turnierserver.gamelogic.interfaces;

import java.io.IOException;

public interface Frontend {
	
	/**
	 * Sendet eine Nachricht an das Frontend
	 * 
	 * @param message Die Nachricht
	 */
	public void sendMessage(byte[] message) throws IOException;
	
}

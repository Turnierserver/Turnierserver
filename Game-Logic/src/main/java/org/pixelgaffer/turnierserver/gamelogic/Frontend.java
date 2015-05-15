package org.pixelgaffer.turnierserver.gamelogic;

public interface Frontend {
	
	/**
	 * Sendet eine Nachricht an das Frontend
	 * 
	 * @param message Die Nachricht
	 */
	public void sendMessage(byte[] message);
	
}

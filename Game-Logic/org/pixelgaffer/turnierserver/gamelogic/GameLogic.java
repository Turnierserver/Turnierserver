package org.pixelgaffer.turnierserver.gamelogic;

public abstract class GameLogic {
	
	public abstract void receiveMessage(String message, int player);
	public abstract void startGame(int playerCount);
	
	protected void sendMessage(String message, int player) {
		
	}
	
}

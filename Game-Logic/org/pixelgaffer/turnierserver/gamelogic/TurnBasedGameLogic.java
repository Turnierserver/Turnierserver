package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.pixelgaffer.turnierserver.backend.AiWrapper;
import org.pixelgaffer.turnierserver.backend.Game;

public abstract class TurnBasedGameLogic<E extends AiObject, R> extends GameLogic<E, R> {
	
	/**
	 * Die AIs, deren Antworten erhalten wurden
	 */
	private Set<AiWrapper> received;
	
	public TurnBasedGameLogic(Class<R> responseType) {
		super(responseType);
		received = new HashSet<>();
	}
	
	/**
	 * Wird aufgerufen, wenn alle AIs geantwortet haben, und der Gamestate geupdated werden muss
	 */
	protected abstract void update();
	
	/**
	 * Verarbeitet eine Antwort einer AI
	 * 
	 * @param message Die Antwort einer AI
	 * @param ai Die AI, welche die Antwort gesendet hat
	 */
	protected abstract void processResponse(R message, AiWrapper ai);
	
	@Override
	protected void receive(R response, AiWrapper ai) {
		if(received.contains(ai)) {
			lost(ai);
			return;
		}
		getUserObject(ai).stopCalculationTimer();
		received.add(ai);
		processResponse(response, ai);
		if(received.size() == game.getAiCount()) {
			update();
			try {
				sendGameState();
				for(AiWrapper wrapper : game.getAis()) {
					getUserObject(wrapper).startCalculationTimer();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			received.clear();
		}
	}
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		try {
			sendGameState();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

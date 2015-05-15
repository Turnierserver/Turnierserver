package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public abstract class TurnBasedGameLogic<E extends AiObject, R> extends GameLogic<E, R> {
	
	/**
	 * Die AIs, deren Antworten erhalten wurden
	 */
	private Set<Ai> received;
	
	public TurnBasedGameLogic(Class<R> responseType) {
		super(responseType);
		received = new HashSet<>();
	}
	
	/**
	 * Wird aufgerufen, wenn alle AIs geantwortet haben, und der Gamestate geupdated werden muss
	 * 
	 * @return Das Objekt für den renderer, wenn null wird nichts gesendet
	 */
	protected abstract Object update();
	
	/**
	 * Verarbeitet eine Antwort einer AI
	 * 
	 * @param message Die Antwort einer AI
	 * @param ai Die AI, welche die Antwort gesendet hat
	 */
	protected abstract void processResponse(R message, Ai ai);
		
	@Override
	protected void receive(R response, Ai ai) {
		if(received.contains(ai)) {
			getUserObject(ai).loose();
			return;
		}
		
		if(getUserObject(ai).stopCalculationTimer()) {
			return;
		}
		received.add(ai);
		processResponse(response, ai);
		
		if(received.size() == game.getAis().size()) {
			Object update = update();
			if(update != null) {
				sendRenderData(update);
			}
			
			try {
				sendGameState();
				for(Ai wrapper : game.getAis()) {
					if(!getUserObject(wrapper).lost) {
						getUserObject(wrapper).startCalculationTimer(10);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			received.clear();
			for(Ai wrapper : game.getAis()) {
				if(getUserObject(wrapper).lost) {
					received.add(wrapper);
				}
			}
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

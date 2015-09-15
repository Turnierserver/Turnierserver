package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.GameState;

/**
 * @param <E>
 *            Das AiObject
 * @param <R>
 *            Die Antwort der Ai
 */
public abstract class GameStateLogic<E extends AiObject, R> extends GameLogic<E, R> {
	
	/**
	 * Der Gamestate
	 */
	protected GameState<?, R> gamestate;
	
	/**
	 * Schickt die Änderungen des GameStates an alle AIs
	 * 
	 * @throws IOException
	 */
	protected final void sendGameState() throws IOException {
		for (Ai ai : game.getAis()) {
			if (!getUserObject(ai).lost) {
				logger.debug("Sende tollen Gamestate an KI " + ai.getId());
				sendGameState(ai);
				logger.debug("Habe tollen Gamestate gesendet!");
			}
		}
	}
	
	/**
	 * Schickt den Gamestate an eine Ai
	 * 
	 * @param ai
	 *            Die Ai, an die der Gamestate geschickt werden soll
	 */
	protected final void sendGameState(Ai ai) throws IOException {
		sendToAi(gamestate.getChanges(ai), ai);
		gamestate.clearChanges(ai);
	}
	
	protected abstract GameState<?, R> createGameState();
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		gamestate = createGameState();
	}
	
}

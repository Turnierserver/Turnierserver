package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;

/**
 * @param <E>
 *            Das AiObject
 * @param <R>
 *            Die Antwort der Ai
 */
public abstract class AlternatingTurnBasedGameLogic<E extends AiObject, R> extends GameStateLogic<E, R> {
	
	/**
	 * Die Ai, die gerade am Zug ist
	 */
	private Ai turn;
	
	/**
	 * Wird aufgerufen, wenn eine AI geantwortet hat
	 * 
	 * @return Das Objekt für den renderer, wenn null wird nichts gesendet
	 */
	protected abstract Object update();
	
	@Override
	protected final void receive(R response, Ai ai, int passedMikros) {
		if (turn == null || turn != ai) {
			logger.critical("Die AI ist nicht an der Reihe, und hat trotzdem etwas gesendet");
			return;
		}
		
		if(getUserObject(ai).subtractMikros(passedMikros)) {
			return;
		}
		
		gamestate.applyChanges(response, ai);
		
		Object update = update();
		if(gameEnded)
			return;
		if (update != null) {
			sendRenderData(update);
		}
		
		turn();
	}
	
	private void turn(Ai ai) {
		turn = ai;
		if(getUserObject(ai).lost) {
			turn();
			return;
		}
		
		try {
			sendGameState(ai);
		} catch (IOException e) {
			getUserObject(ai).loose("Es gab ein Problem bei der Kommunikation mit der KI");
		}
	}
	
	private void turn() {
		if (turn == null) {
			turn(game.getAis().get(0));
			return;
		}
		if (turn.getIndex() == game.getAis().size() - 1) {
			if (allRoundsPlayed()) {
				endGame("Die maximale Anzahl an Runden (" + maxTurns + ") wurde gespielt");
				return;
			}
			round();
		}
		turn(game.getAis().get((turn.getIndex() + 1) % game.getAis().size()));
	}
	
	/**
	 * super.lost(Ai ai) MUSS AUFGERUFEN WERDEN!! Es ist möglich, dass das Spiel vorbei ist, sobald diese Methode zurückgibt
	 */
	@Override
	public void lost(Ai ai) {
		logger.debug("ai " + ai.getId() + " hat verloren");
		logger.debug("Ai " + ai.getId() + " ist momentan am zug");
		if (ai.getIndex() == turn.getIndex()) {
			logger.debug("calling turn");
			turn();
		}
	}
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		turn();
	}
	
}

package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;

import com.google.gson.reflect.TypeToken;

/**
 * @param <E> Das AiObject
 * @param <R> Die Antwort der Ai
 */
public abstract class AlternatingTurnBasedGameLogic<E extends AiObject, R> extends GameStateLogic<E, R> {
	
	public AlternatingTurnBasedGameLogic(TypeToken<R> token) {
		super(token);
	}

	/**
	 * Die Ai, die gerade am Zug ist
	 */
	private Ai turn;
	
	/**
	 * Wird aufgerufen, wenn alle AIs geantwortet haben, und der Gamestate geupdated werden muss
	 * 
	 * @return Das Objekt für den renderer, wenn null wird nichts gesendet
	 */
	protected abstract Object update();
	
	@Override
	protected final void receive(R response, Ai ai) {
		if(turn == null || turn != ai) {
			getUserObject(ai).loose();
			return;
		}
		
		if(getUserObject(ai).stopCalculationTimer()) {
			return;
		}
		
		gamestate.applyChanges(response, ai);
		
		Object update = update();
		if(update != null) {
			sendRenderData(update);
		}
		
		turn();
	}
	
	private void turn(Ai ai) {
		turn = game.getAis().get((ai.getIndex() + 1) % game.getAis().size());
		try {
			sendGameState(ai);
			getUserObject(ai).startCalculationTimer(10);
		} catch (IOException e) {
			getUserObject(ai).loose();
		}
	}
	
	private void turn() {
		if(turn == null) {
			turn(game.getAis().get(0));
			return;
		}
		if(turn.getIndex() == game.getAis().size() - 1) {
			if(maxTurns == playedRounds) {
				endGame();
				return;
			}
			round();
		}
		turn(game.getAis().get((turn.getIndex() + 1) % game.getAis().size()));
	}
	
	/**
	 * super.lost(Ai ai) MUSS AUFGERUFEN WERDEN!!
	 * Es ist möglich, dass das Spiel vorbei ist, sobald diese Methode zurückgibt
	 */
	@Override
	public void lost(Ai ai) {
		if(ai == turn) {
			turn();
		}
	}
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		turn();
	}
	
}

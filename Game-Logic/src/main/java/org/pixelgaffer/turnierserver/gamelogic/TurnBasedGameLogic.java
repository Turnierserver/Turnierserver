package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;

import com.google.gson.reflect.TypeToken;

/**
 * @param <E>
 *            Das AiObject
 * @param <R>
 *            Die Antwort der Ai
 */
public abstract class TurnBasedGameLogic<E extends AiObject, R> extends GameStateLogic<E, R> {
	
	public TurnBasedGameLogic(TypeToken<R> token) {
		super(token);
	}
	
	/**
	 * Die AIs, deren Antworten erhalten wurden
	 */
	private List<Ai> received = new ArrayList<>();
	
	/**
	 * Wird aufgerufen, wenn alle AIs geantwortet haben, und der Gamestate geupdated werden muss
	 * 
	 * @return Das Objekt für den renderer, wenn null wird nichts gesendet
	 */
	protected abstract Object update();
	
	@Override
	protected final void receive(R response, Ai ai, int passedMillis) {
		if (getUserObject(ai).lost) {
			logger.warning("Verlorene KI schickt noch stuff; wird ignoriert [" + ai.getId() + "]");
			return;
		}
		logger.debug("Habe tolle Sachen von KI " + ai.getId() + " emfangen!");
		
		if (received.contains(ai)) {
			logger.critical("Habe von einer KI zweimal was emfangen: " + ai.getId());
			return;
		}
		
		if(getUserObject(ai).subtractMillis(passedMillis)) {
			return;
		}
		
		received.add(ai);
		gamestate.applyChanges(response, ai);
		
		check();
	}
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		started = true;
		try {
			sendGameState();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * super.lost(Ai ai) MUSS AUFGERUFEN WERDEN (auser das Spiel wird beendet)!!
	 */
	@Override
	public void lost(Ai ai) {
		if (!received.contains(ai)) {
			received.add(ai);
			check();
		}
	}
	
	private void check() {
		if (received.size() == game.getAis().size()) {
			Object update = update();
			if (gameEnded) {
				return;
			}
			if (update != null) {
				sendRenderData(update);
			}
			
			round();
			
			if (allRoundsPlayed()) {
				endGame("Die maximale Anzahl an Runden (" + maxTurns + ") wurde gespielt");
				return;
			}
			
			received.clear();

			try {
				sendGameState();
			} catch (IOException e) {
				e.printStackTrace();
			}
						
			for (Ai wrapper : game.getAis()) {
				if (getUserObject(wrapper).lost) {
					received.add(wrapper);
				}
			}
		}
	}
	
}

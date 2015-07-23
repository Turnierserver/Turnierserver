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
    protected final void receive(R response, Ai ai) {
	if (received.contains(ai)) {
	    getUserObject(ai).loose();
	    return;
	}

	if (getUserObject(ai).stopCalculationTimer()) {
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
     * super.lost(Ai ai) MUSS AUFGERUFEN WERDEN!!
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

	    if (maxTurns == playedRounds && maxTurns > 0) {
		endGame();
		return;
	    }

	    try {
		sendGameState();
		for (Ai wrapper : game.getAis()) {
		    if (!getUserObject(wrapper).lost) {
			getUserObject(wrapper).startCalculationTimer(10);
		    }
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    received.clear();
	    for (Ai wrapper : game.getAis()) {
		if (getUserObject(wrapper).lost) {
		    received.add(wrapper);
		}
	    }
	}
    }

}

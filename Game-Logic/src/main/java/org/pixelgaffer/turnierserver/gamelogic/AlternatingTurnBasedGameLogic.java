/*
 * AlternatingTurnBasedGameLogic.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;

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
		if (update != null) {
			sendRenderData(update);
		}
		
		turn();
	}
	
	private void turn(Ai ai) {
		turn = game.getAis().get((ai.getIndex() + 1) % game.getAis().size());
		try {
			sendGameState(ai);
		} catch (IOException e) {
			getUserObject(ai).loose("Es gab ein Problem mit der Kommunikation mit der KI");
		}
	}
	
	private void turn() {
		if (turn == null) {
			turn(game.getAis().get(0));
			return;
		}
		if (turn.getIndex() == game.getAis().size() - 1) {
			if (maxTurns == playedRounds) {
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
		if (ai == turn) {
			turn();
		}
	}
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		turn();
	}
	
}

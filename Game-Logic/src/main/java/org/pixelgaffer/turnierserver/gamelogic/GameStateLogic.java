/*
 * GameStateLogic.java
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
import org.pixelgaffer.turnierserver.gamelogic.interfaces.GameState;

import com.google.gson.reflect.TypeToken;

/**
 * @param <E>
 *            Das AiObject
 * @param <R>
 *            Die Antwort der Ai
 */
public abstract class GameStateLogic<E extends AiObject, R> extends GameLogic<E, R> {
	
	public GameStateLogic(TypeToken<R> token) {
		super(token);
	}
	
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

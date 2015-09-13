/*
 * OneBuilderAllSolverLogic.java
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverAiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverGameState;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;
import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverResponse;

import com.google.gson.reflect.TypeToken;

/**
 * @param <E>
 *            Das AiObject
 * @param <G>
 *            Der GameState
 * @param <B>
 *            Die BuilderResponse
 * @param <S>
 *            Die SolverResponse
 */
public abstract class OneBuilderAllSolverLogic<E extends BuilderSolverAiObject<G>, G extends BuilderSolverGameState<?, B, S>, B, S> extends BuilderSolverLogic<E, G, B, S> {
	
	public OneBuilderAllSolverLogic(TypeToken<BuilderSolverResponse<B, S>> token) {
		super(token);
	}
	
	@Override
	public List<Ai> getBuilder() {
		return new ArrayList<>(Arrays.asList(getBuilder(null)));
	}
	
	@Override
	public List<Ai> getSolver() {
		List<Ai> solver = new ArrayList<>(game.getAis());
		solver.remove(getBuilder(null));
		return solver;
	}
	
	@Override
	public Ai getBuilder(Ai ai) {
		return game.getAis().get(playedRounds - 1);
	}
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		maxTurns = game.getAis().size();
	}
	
}

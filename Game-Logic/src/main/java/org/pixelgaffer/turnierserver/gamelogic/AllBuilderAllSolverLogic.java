package org.pixelgaffer.turnierserver.gamelogic;

import java.util.ArrayList;
import java.util.List;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverAiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverGameState;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;
import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverResponse;

import com.google.gson.reflect.TypeToken;

/**
 * @param <E> Das AiObject
 * @param <G> Der GameState
 * @param <B> Die BuilderResponse
 * @param <S> Die SolverResponse
 */
public abstract class AllBuilderAllSolverLogic<E extends BuilderSolverAiObject<G>, G extends BuilderSolverGameState<?, B, S>, B, S> extends BuilderSolverLogic<E, G, B, S> {
	
	public AllBuilderAllSolverLogic(TypeToken<BuilderSolverResponse<B, S>> token) {
		super(token);
	}

	@Override
	public List<Ai> getBuilder() {
		return new ArrayList<>(game.getAis());
	}
	
	@Override
	public List<Ai> getSolver() {
		return new ArrayList<>(game.getAis());
	}
	
	@Override
	public Ai getBuilder(Ai ai) {
		return game.getAis().get((ai.getIndex() - getPlayedRounds()) % game.getAis().size());
	}
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		setMaxTurns(game.getAis().size() - 1);
	}
	
}

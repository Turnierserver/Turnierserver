package org.pixelgaffer.turnierserver.gamelogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverAiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;

public abstract class OneBuilderAllSolverLogic<E extends BuilderSolverAiObject<B,S>, B, S> extends BuilderSolverLogic<E, B, S> {
	
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
		return game.getAis().get(getPlayedRounds() - 1);
	}
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		setMaxTurns(game.getAis().size());
	}
	
}

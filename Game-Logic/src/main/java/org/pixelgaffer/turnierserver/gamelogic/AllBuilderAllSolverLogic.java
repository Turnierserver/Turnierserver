package org.pixelgaffer.turnierserver.gamelogic;

import java.util.ArrayList;
import java.util.List;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverAiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;

public abstract class AllBuilderAllSolverLogic<E extends BuilderSolverAiObject<B,S>, B, S> extends BuilderSolverLogic<E, B, S> {
	
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

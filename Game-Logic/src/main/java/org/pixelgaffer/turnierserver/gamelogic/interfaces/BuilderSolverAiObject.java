package org.pixelgaffer.turnierserver.gamelogic.interfaces;

public class BuilderSolverAiObject<B, S> extends AiObject {
	
	public BuilderSolverGameState<?, B, S> building;
	public BuilderSolverGameState<?, B, S> solving;
	public boolean succesful = false;
	
}

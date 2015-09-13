package org.pixelgaffer.turnierserver.gamelogic.interfaces;

public class BuilderSolverAiObject<E extends BuilderSolverGameState<?, ?, ?>> extends AiObject {
	
	public E building;
	public E solving;
	public boolean succesful = false;
	
}

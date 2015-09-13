package org.pixelgaffer.turnierserver.gamelogic.messages;

public class BuilderSolverResponse<B, S> {
	
	public B build;
	public S solve;
	
	@Override
	public String toString() {
		return build != null ? build.toString() : solve.toString();
	}
	
}

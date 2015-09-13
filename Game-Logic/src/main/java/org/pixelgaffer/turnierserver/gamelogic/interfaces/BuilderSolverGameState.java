/*
 * BuilderSolverGameState.java
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
package org.pixelgaffer.turnierserver.gamelogic.interfaces;

import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverChange;
import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverResponse;

import lombok.Getter;
import lombok.Setter;

/**
 * @param <C>
 *            Das Veränderungsobjekt, welches an die Ai gesendet werden soll
 * @param <B>
 *            Die BuildResponse
 * @param <B>
 *            Die SolveResponse
 */
public abstract class BuilderSolverGameState<C, B, S> implements GameState<BuilderSolverChange<C>, BuilderSolverResponse<B, S>> {
	
	@Getter
	@Setter
	private Ai ai;
	
	public static class Response<C> {
		
		/**
		 * True wenn die Antwort der Ai valide war
		 */
		public boolean valid;
		/**
		 * True wenn die Aufgabe mit der Antwort abgeschlossen ist
		 */
		public boolean finished;
		/**
		 * Die Änderungen, die während diesem Schritt vorgenommen wurden. Null, wenn nichts gesendet werden soll.
		 */
		public C changes;
		/**
		 * Die Renderdaten. Null wenn nichts gesendet werden soll
		 */
		public Object renderData;
	}
	
	/**
	 * Builded den GameState nach der Antwort der Ai, wird nur auf der GameLogic aufgerufen
	 * 
	 * @param response Die Antwort der Ai
	 * @param ai Die Ai, welche geantwortet hat
	 * @return Die BuildResponse
	 */
	public abstract Response<C> build(B response);
	
	/**
	 * Solved den GameState nach der Antwort der Ai, wird nur auf der GameLogic aufgerufen
	 * 
	 * @param response Die Antwort der Ai
	 * @param ai Die Ai, welche geantwortet hat
	 * @return Die SolveResponse
	 */
	public abstract Response<C> solve(S response);
	
	/**
	 * Gibt ein Objekt zurück, welches an die Ai geschickt werden kann, damit diese diesen Zustand erhält bevor sie anfängt zu lösen
	 * 
	 * @return Diesen Zustand als Änderungsobjekt
	 */
	public abstract C getState();
	
	@Override
	public final void applyChanges(BuilderSolverResponse<B, S> response, Ai ai) {
		throw new IllegalStateException();
	}
	
	@Override
	public BuilderSolverChange<C> getChanges(Ai ai) {
		throw new IllegalStateException();
	}
	
	@Override
	public void clearChanges(Ai ai) {
		throw new IllegalStateException();
	}
	
}

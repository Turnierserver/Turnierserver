package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverAiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverGameState;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverGameState.Response;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;
import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverChange;
import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverResponse;

/**
 * @param <E> Das AiObject
 * @param <G> Der GameState
 * @param <B> Die BuilderResponse
 * @param <S> Die SolverResponse
 */
public abstract class BuilderSolverLogic<E extends BuilderSolverAiObject<G>, G extends BuilderSolverGameState<?, B, S>, B, S> extends GameLogic<E, BuilderSolverResponse<B, S>> {
	
	/**
	 * True wenn building, sonst solving
	 */
	private boolean building;
	
	/**
	 * Die Liste mit den Ais, die ihre momentane Aufgabe schon erfüllt haben
	 */
	private List<Ai> finished = new ArrayList<>();
	
	/**
	 * Gibt eine Liste mit allen Ais zurück, die Builden sollen
	 * 
	 * @return Die Liste mit allen Ais, die Builden sollen
	 */
	public abstract List<Ai> getBuilder();
	
	/**
	 * Gibt eine Liste mit allen Ais zurück, die Solven sollen
	 * 
	 * @return Die Liste mit allen Ais, die Solven sollen
	 */
	public abstract List<Ai> getSolver();
	
	/**
	 * Erstellt einen neuen GameState, damit die ai diesen aufbauen kann
	 * 
	 * @param ai Die Ai, die diesen aufbauen wird
	 * @return Den neuen GameState
	 */
	public abstract G createGameState(Ai ai);
	
	/**
	 * Wird aufgerufen, wenn eine Ai beim ausführen ihrer Aufgabe gescheitert ist
	 * 
	 * @param building True wenn die Aufgabe Builden war, False wenn die Aufgabe Solven war.
	 * @param ai Die Ai, welche gescheitert ist
	 */
	public abstract void failed(boolean building, Ai ai);
	
	/**
	 * Wird aufgerufen, wenn eine Ai beim ausführen ihrer Aufgabe erfoglreich war
	 * 
	 * @param building True wenn die Aufgabe Builden war, Fale wenn die Aufgabe Solven war.
	 * @param ai Die Ai, welche erflogreich war
	 */
	public abstract void succeeded(boolean building, Ai ai);
	
	/**
	 * Gibt die Ai zurück, welche das Spielefeld für die gegebene Ai gebuildet hat
	 * 
	 * @param ai Die Ai, für welche der Builder bestimmt werden soll
	 * @return Die Ai, welche das Spielfeld gebuildet hat
	 */
	public abstract Ai getBuilder(Ai ai);
	
	@Override
	public void startGame(Game game) {
		super.startGame(game);
		startBuilding();
	}
	
	/**
	 * super.lost(Ai ai) MUSS AUFGERUFEN WERDEN!!
	 */
	@Override
	public void lost(Ai ai) {
		List<Ai> list = building ? getBuilder() : getSolver();
		if(list.contains(ai)) {
			if(!finished.contains(ai)) {
				finished.add(ai);
				failed(building, ai);
				check();
			}
		}
	}
	
	@Override
	protected void receive(BuilderSolverResponse<B, S> response, Ai ai) {
		if(finished.contains(ai)) {
			getUserObject(ai).loose();
			return;
		}
		
		if(getUserObject(ai).stopCalculationTimer()) {
			return;
		}
		
		Response<?> result = null;
		if(building) {
			if(response.build == null) {
				getUserObject(ai).loose();
				return;
			}
			result = getUserObject(ai).building.build(response.build);
		}
		else {
			if(response.solve == null) {
				getUserObject(ai).loose();
				return;
			}
			result = getUserObject(ai).solving.solve(response.solve);
		}
			
		if(result.renderData != null) {
			sendToFronted(result.renderData);
		}
		if(result.changes != null) {
			try {
				sendToAi(result.changes, ai);
				getUserObject(ai).startCalculationTimer(10);
			} catch (IOException e) {
				getUserObject(ai).loose();
			}
		}
		if(result.finished) {
			if(getUserObject(ai).stopCalculationTimer()) {
				return;
			}
			finished.add(ai);
			if(!result.valid) {
				failed(building, ai);
			}
			else {
				succeeded(building, ai);
				getUserObject(ai).succesful = true;
			}
			check();
		}
		return;
		
	}
	
	private void check() {
		if(finished.size() != game.getAis().size()) {
			return;
		}
		
		if(getMaxTurns() == getPlayedRounds()) {
			endGame();
			return;
		}
		
		finished.clear();
		
		if(building) {
			startSolving();
		}
		else {
			round();
			startBuilding();
		}
	}
	
	private void startSolving() {
		building = false;
		BuilderSolverChange<Object> change = new BuilderSolverChange<>();
		change.building = false;
		for(Ai ai : getSolver()) {
			if(getUserObject(ai).lost) {
				finished.add(ai);
				return;
			}
			Ai builder = getBuilder(ai);
			if(!getUserObject(builder).succesful || getUserObject(ai).lost) {
				succeeded(false, ai);
				finished.add(ai);
				return;
			}
			getUserObject(ai).solving = getUserObject(builder).building;
			getUserObject(ai).solving.setAi(ai);
			change.change = getUserObject(builder).building.getState();
			getUserObject(ai).succesful = false;
			try {
				sendToAi(change, ai);
				getUserObject(ai).startCalculationTimer(10);
			} catch (IOException e) {
				getUserObject(ai).loose();
			}
		}
	}
	
	private void startBuilding() {
		building = true;
		BuilderSolverChange<Object> change = new BuilderSolverChange<>();
		change.building = true;
		for(Ai ai : getBuilder()) {
			if(getUserObject(ai).lost) {
				finished.add(ai);
				continue;
			}
			getUserObject(ai).building = createGameState(ai);
			getUserObject(ai).succesful = false;
			try {
				sendToAi(change, ai);
				getUserObject(ai).startCalculationTimer(10);
			} catch (IOException e) {
				getUserObject(ai).loose();
			}
		}
	}
		
}

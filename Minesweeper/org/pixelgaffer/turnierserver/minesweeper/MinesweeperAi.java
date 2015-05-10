package org.pixelgaffer.turnierserver.minesweeper;

import org.pixelgaffer.turnierserver.ailibrary.Ai;

public abstract class MinesweeperAi extends Ai<MinesweeperState, MinesweeperResponse> {

	public MinesweeperAi(String[] args) {
		super(args);
	}

	private int xFlag = -1, yFlag = -1;
	private int xStep = -1, yStep = -1;
	
	@Override
	protected MinesweeperResponse update(MinesweeperState state) {
		MinesweeperResponse response = new MinesweeperResponse();
		if(state.isGenerating()) {
			response.newField = Cell.toMap(generateField());
			return response;
		}
		step(state);
		response.xFlag = xFlag;
		response.yFlag = yFlag;
		response.xStep = xStep;
		response.yStep = yStep;
		return response;
	}

	@Override
	protected MinesweeperState getState() {
		return new MinesweeperState(Cell.toCellArray(gamestate), Boolean.parseBoolean(gamestate.get("creating")));
	}
	
	/**
	 * Muss das Feld generieren. Das Feld muss Cell.BOMB_COUNT bomben enthalten
	 * 
	 * @return Das generierte Feld
	 */
	protected abstract Cell[][] generateField();
	/**
	 * Macht einen Schritt. Es kann jeweils maximal ein Feld geflagged und aufgedeckt werden
	 * 
	 * @param state Der Spielzustand
	 */
	protected abstract void step(MinesweeperState state);
	
	/**
	 * Markiert ein Feld
	 * 
	 * @param x Die x-Koordinate des Feldes
	 * @param y Die y-Koordinate des Feldes
	 */
	public void flag(int x, int y) {
		if(!Cell.isInField(x, y)) {
			return;
		}
		xFlag = x;
		yFlag = y;
	}
	
	/**
	 * Löscht die Flagge, die erstellt werden soll
	 */
	public void unflag() {
		xFlag = -1;
		yFlag = -1;
	}
	
	public void uncover(int x, int y) {
		if(!Cell.isInField(x, y)) {
			return;
		}
		xStep = x;
		yStep = y;
	}
	
}

package org.pixelgaffer.turnierserver.minesweeper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MinesweeperState {
	
	/**
	 * Das Spielfeld
	 */
	@Getter
	private Cell[][] field;
	
	/**
	 * True, wenn diese AI diese Runde das Spielfeld generieren soll
	 */
	@Getter
	private boolean generating;
	
}

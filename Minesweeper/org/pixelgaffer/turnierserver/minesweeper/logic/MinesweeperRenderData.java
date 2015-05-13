package org.pixelgaffer.turnierserver.minesweeper.logic;

import java.util.Map;

import org.msgpack.annotation.Message;

@Message
public class MinesweeperRenderData {
	
	public Map<String, String> field;
	public String output;
	public int calculationTime;
	public int aiID;
	
}

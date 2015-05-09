package org.pixelgaffer.turnierserver.minesweeper;

import java.util.Map;

import org.msgpack.annotation.Message;

@Message
public class MinesweeperRenderData {
	
	public Map<String, String> field;
	public String output;
	public int solving;
	public int calculationTime;
	
}

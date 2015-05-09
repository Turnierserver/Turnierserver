package org.pixelgaffer.turnierserver.minesweeper;

import java.util.Map;

import org.msgpack.annotation.Message;
import org.msgpack.annotation.Optional;

@Message
public class MinesweeperResponse  {
	
	@Optional
	public Map<String, String> newField;
	@Optional
	public int xFlag = -1, yFlag = -1;
	@Optional
	public int xStep = -1, yStep = -1;
	@Optional
	public String output;
	
}

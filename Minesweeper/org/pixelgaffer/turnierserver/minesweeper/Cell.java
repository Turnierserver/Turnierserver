package org.pixelgaffer.turnierserver.minesweeper;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class Cell {
	
	public static final int FIELD_SIZE = 8;
	public static final int BOMB_COUNT = 12;
	
	public enum Type {
		BOMB, EMPTY, COVERED;
	}
	
	@Getter @Setter
	private boolean flagged;
	@Getter
	private boolean uncovered;
	@Getter
	private int bombsArround;
	@Getter
	private Type type;
	
	/**
	 * Erstellt eine neue Zelle. TYP DARF BEIM GENERIEREN NICHT COVERED SEIN!
	 * 
	 * @param type
	 */
	public Cell(Type type) {
		this.type = type;
	}
	
	private Cell() {}
	
	void countSurroundingBombs(Cell[][] field, int x, int y) {
		bombsArround = 0;
		bombsArround += isBomb(x + 1, y, field);
		bombsArround += isBomb(x + 1, y + 1, field);
		bombsArround += isBomb(x + 1, y - 1, field);
		bombsArround += isBomb(x, y + 1, field);
		bombsArround += isBomb(x, y - 1, field);
		bombsArround += isBomb(x - 1, y, field);
		bombsArround += isBomb(x - 1, y + 1, field);
		bombsArround += isBomb(x - 1, y - 1, field);
	}
	
	int isBomb(int x, int y, Cell[][] field) {
		if(x < 0 || x >= FIELD_SIZE || y < 0 || y >= FIELD_SIZE) {
			return 0;
		}
		return field[x][y].type == Type.BOMB ? 1 : 0;
	}
	
	Map<String, String> uncover(Cell[][] field, int x, int y) {	
		return uncover(field, x, y, new HashMap<>());
	}
	
	private Map<String, String> uncover(Cell[][] field, int x, int y, Map<String, String> map) {	
		if(type == Type.BOMB) {
			return null;
		}
		uncovered = true;
		map.put(x + ":" + y, toString());
		if(bombsArround == 0) {
			uncover(field, x + 1, y, map);
			uncover(field, x + 1, y + 1, map);
			uncover(field, x + 1, y - 1, map);
			uncover(field, x, y + 1, map);
			uncover(field, x, y - 1, map);
			uncover(field, x - 1, y, map);
			uncover(field, x - 1, y + 1, map);
			uncover(field, x - 1, y - 1, map);
		}
		return map;
	}
	
	@Override
	public String toString() {
		if(!uncovered) {
			return "0 " + Boolean.toString(flagged);
		}
		if(type == Type.BOMB) {
			return "1";
		}
		return "2 " + bombsArround;
	}
	
	static Cell fromString(String string) {
		Cell cell = new Cell();
		if(string.startsWith("0")) {
			cell.type = Type.COVERED;
			cell.flagged = Boolean.parseBoolean(string.split(" ")[1]);
			return cell;
		}
		if(string.equals("1")) {
			cell.type = Type.BOMB;
			cell.uncovered = true;
			return cell;
		}
		if(string.startsWith("2")) {
			cell.type = Type.EMPTY;
			cell.uncovered = true;
			cell.bombsArround = Integer.parseInt(string.split(" ")[1]);
			return cell;
		}
		return null;
	}
	
	static Cell[][] toCellArray(Map<String, String> map) {
		Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];
		
		for(int i = 0; i < FIELD_SIZE; i++) {
			for(int j = 0; j < FIELD_SIZE; j++) {
				field[i][j] = fromString(map.get(i + ":" + j));
			}
		}
		
		return field;
	}
	
	static Map<String, String> toMap(Cell[][] field) {
		if(field == null) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		
		for(int i = 0; i < field.length; i++) {
			for(int j = 0; j < field.length; j++) {
				map.put(i + ":" + j, field[i][j].toString());
			}
		}
		
		return map;
	}
	
	public static boolean isInField(int x, int y) {
		return x >= 0 && x < FIELD_SIZE && y >= 0 && y < FIELD_SIZE;
	}
	
}

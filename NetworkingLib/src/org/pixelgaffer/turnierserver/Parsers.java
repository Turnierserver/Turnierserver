package org.pixelgaffer.turnierserver;

public class Parsers {
	
	private static Parser defaultParser;
	
	public static Parser getParser(boolean encrypt) {
		if(encrypt) {
			return null;
		} else {
			return defaultParser;
		}
	}
	
	public static Parser getParser() {
		return getParser(false);
	}
	
}

package org.pixelgaffer.turnierserver;

public class Parsers {
	
	private static Parser defaultParser;
	private static Parser compressingParser;
	
	public static Parser getParser(boolean compress) {
		if(compress) {
			if(compressingParser == null) {
				compressingParser = new GsonGzipParser();
			}
			return compressingParser;
		} else {
			if(defaultParser == null) {
				defaultParser = new GsonParser();
			}
			return defaultParser;
		}
	}
	
	public static Parser getParser() {
		return getParser(false);
	}
	
}

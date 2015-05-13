package org.pixelgaffer.turnierserver;

public class Parsers {
	
	private static Parser defaultParser;
	private static Parser compressingParser;
	private static boolean compressByDefault;
	private static boolean compressWorkerByDefault;
	private static boolean compressFrontendByDefault;
	private static boolean compressSandboxByDefault;
	
	static {
		compressByDefault = Boolean.parseBoolean(System.getProperty("turnierserver.serializer.compress"));
		compressWorkerByDefault = Boolean.parseBoolean(System.getProperty("turnierserver.serializer.compress.worker"));
		compressFrontendByDefault = Boolean.parseBoolean(System.getProperty("turnierserver.serializer.compress.frontend"));
		compressSandboxByDefault = Boolean.parseBoolean(System.getProperty("turnierserver.serializer.compress.sandbox"));
	}
	
	
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
		return getParser(compressByDefault);
	}
	
	public static Parser getWorker() {
		return getParser(compressWorkerByDefault);
	}
	
	public static Parser getFrontend() {
		return getParser(compressFrontendByDefault);
	}
	
	public static Parser getSandbox() {
		return getParser(compressSandboxByDefault);
	}
	
}

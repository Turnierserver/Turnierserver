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
	
	/**
	 * Gibt einen Parser zurück
	 * 
	 * @param compress True wenn die Nachrichten komprimiert werden sollen
	 * @return Den Parser
	 */
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
	
	/**
	 * Gibt einen Parser zurück. Die System-Property "turnierserver.serializer.compress" gibt an, ob der Parser den diese Methode zurückgibt, komprimiert.
	 * 
	 * @return Einen Parser
	 */
	public static Parser getParser() {
		return getParser(compressByDefault);
	}
	
	/**
	 * Gibt einen Parser zurück, der für die Kommunikation zu den Workern verwendet werden soll. Die System-Property "turnierserver.serializer.compress.worker" gibt an, ob der Parser den diese Methode zurückgibt, komprimiert.
	 * 
	 * @return Einen Parser
	 */
	public static Parser getWorker() {
		return getParser(compressWorkerByDefault);
	}
	
	/**
	 * Gibt einen Parser zurück, der für die Kommunikation zum Frontend verwendet werden soll. Die System-Property "turnierserver.serializer.compress.frontend" gibt an, ob der Parser den diese Methode zurückgibt, komprimiert.
	 * 
	 * @return Einen Parser
	 */
	public static Parser getFrontend() {
		return getParser(compressFrontendByDefault);
	}
	
	/**
	 * Gibt einen Parser zurück, der für die Kommunikation zur Sandbox verwendet werden soll. Die System-Property "turnierserver.serializer.compress.sandbox" gibt an, ob der Parser den diese Methode zurückgibt, komprimiert.
	 * 
	 * @return Einen Parser
	 */
	public static Parser getSandbox() {
		return getParser(compressSandboxByDefault);
	}
	
}

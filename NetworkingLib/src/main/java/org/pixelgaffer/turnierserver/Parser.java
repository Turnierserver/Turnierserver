package org.pixelgaffer.turnierserver;

import java.io.IOException;

import com.google.gson.reflect.TypeToken;

public interface Parser {
	
	/**
	 * Parsed ein Byte-Array zu einem der Klasse angehörigen Objekt
	 * 
	 * @param data Das Byte-Array, das geparsed werden soll
	 * @param type Die Klasse des Objektes, zu dem das Byte[] geparsed werden soll
	 * @return Das geparste Objekt
	 * @throws IOException
	 */
	public <E> E parse(byte[] data, Class<E> type) throws IOException;
	/**
	 * Parsed ein Byte-Array zu einem des GenericTypes des TypeToken angehörigem Objekt
	 * 
	 * @param data Das Byte-Array, das geparsed werden soll
	 * @param token Das TypeToken, zu dessem Generic-Type das Byte-Array geparsed werden soll
	 * @return Das geparste Objekt
	 * @throws IOException
	 */
	public <E> E parse(byte[] data, TypeToken<E> token) throws IOException;
	
	/**
	 * Parsed ein Objekt in ein Byte-Array
	 * 
	 * @param obj Das Objekt, das geparsed werden sollte
	 * @return Das geparste Objekt
	 * @throws IOException
	 */
	public byte[] parse(Object obj) throws IOException;
	
}

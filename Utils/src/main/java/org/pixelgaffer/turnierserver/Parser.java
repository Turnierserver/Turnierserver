/*
 * Parser.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pixelgaffer.turnierserver;

import java.io.IOException;
import java.lang.reflect.Type;

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
	public <E> E parse(byte[] data, Type type) throws IOException;
	
	/**
	 * Parsed ein Objekt in ein Byte-Array
	 * 
	 * @param obj Das Objekt, das geparsed werden sollte
	 * @return Das geparste Objekt
	 * @throws IOException
	 */
	public byte[] parse(Object obj, boolean newline) throws IOException;
	
}

/*
 * Parsers.java
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Since;
import com.google.gson.reflect.TypeToken;

public class Parsers {
	
	private static Parser defaultParser;
	private static Parser compressingParser;
	
	private static GsonBuilder gson = new GsonBuilder();
	
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
				compressingParser = new GsonGzipParser(gson);
			}
			return compressingParser;
		} else {
			if(defaultParser == null) {
				defaultParser = new GsonParser(gson);
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
	
	/**
	 * Setzt die Version des GSONS. Dies bedeutet, dass alle Felder, welche mit einer neueren Version annotiert wurden (siehe {@link Since}), nicht serialisiert/deserialisiert werden
	 * 
	 * @param version Die Version
	 * @see Since
	 */
	public static void setVersion(double version) {
		gson.setVersion(version);
	}
	
	/**
	 * Fügt einen {@link TypeAdapter} für ein {@link TypeToken} hinzu.
	 * 
	 * @param type Das TypeToken
	 * @param typeAdapter Der TypeAdapter
	 * @see TypeToken
	 * @see TypeAdapter
	 */
	public static <E> void addTypeAdapter(TypeToken<E> type, TypeAdapter<E> typeAdapter) {
		gson.registerTypeAdapter(type.getType(), typeAdapter);
	}
	
	public static void escape(byte[] bytes, OutputStream out) {
		try {
			for(byte b : bytes) {
				if(b == (byte) '\\') {
					out.write('\\');
					out.write('\\');
					continue;
				}
				if(b == (byte) '\n') {
					out.write('\\');
					out.write('n');
					continue;
				}
				out.write(b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] escape(byte[] in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for(int i = 0; i < in.length; i++) {
			if(in[i] == '\\') {
				if(in[i + 1] == 'n') {
					out.write('\n');
					i++;
					continue;
				}
				if(in[i + 1] == '\\') {
					out.write('\\');
					i++;
					continue;
				}
			}
			out.write(in[i]);
		}
		return out.toByteArray();
	}
	
}

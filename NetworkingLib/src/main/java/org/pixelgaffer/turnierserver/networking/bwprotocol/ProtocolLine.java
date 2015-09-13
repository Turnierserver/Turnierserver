/*
 * ProtocolLine.java
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
package org.pixelgaffer.turnierserver.networking.bwprotocol;

import java.io.IOException;
import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.networking.messages.SandboxMessage;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;

@AllArgsConstructor
@ToString
public class ProtocolLine
{
	/** Enthält ein {@link WorkerCommandAnswer}-Objekt. */
	public static final byte ANSWER = 'A';
	/** Enthält ein {@link WorkerInfo}-Objekt. */
	public static final byte INFO = 'I';
	/** Enthält ein {@link AiConnected}-Objekt. */
	public static final byte AICONNECTED = 'C';
	/** Enthält ein {@link SandboxMessage}-Objekt. */
	public static final byte SANDBOX_MESSAGE = 'M';
	
	/** Der Inhalt der Zeile. */
	@Getter
	private byte mode;
	
	/** Das Objekt dieser Zeile. */
	@Getter
	private Object object;
	
	@SuppressWarnings("unchecked")
	public <T> T getObject (Class<T> clazz)
	{
		return (T)getObject();
	}
	
	/**
	 * Parst die Zeile.
	 */
	public ProtocolLine (byte line[]) throws IOException
	{
		mode = line[0];
		line = Arrays.copyOfRange(line, 1, line.length);
		switch (mode)
		{
			case ANSWER:
				object = Parsers.getWorker().parse(line, WorkerCommandAnswer.class);
				break;
			case INFO:
				object = Parsers.getWorker().parse(line, WorkerInfo.class);
				break;
			case AICONNECTED:
				object = Parsers.getWorker().parse(line, AiConnected.class);
				break;
			case SANDBOX_MESSAGE:
				object = Parsers.getWorker().parse(line, SandboxMessage.class);
				break;
			default:
				System.err.println("ProtocolLine: Fehler: Unbekannter mode: " + ((char)mode));
		}
	}
	
	/**
	 * Serialisiert dieses Objekt.
	 */
	public byte[] serialize (boolean newline) throws IOException
	{
		byte obj[] = Parsers.getWorker().parse(getObject(), newline);
		byte data[] = new byte[obj.length + 1];
		data[0] = getMode();
		System.arraycopy(obj, 0, data, 1, obj.length);
		return data;
	}
}

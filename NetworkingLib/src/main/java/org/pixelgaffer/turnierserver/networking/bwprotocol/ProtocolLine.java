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

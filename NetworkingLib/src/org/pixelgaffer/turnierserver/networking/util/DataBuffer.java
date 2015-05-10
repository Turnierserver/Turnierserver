package org.pixelgaffer.turnierserver.networking.util;

import java.io.ByteArrayOutputStream;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Diese Klasse ist ein Buffer für ein byte[], der von Paket-basierten Clients
 * genutzt wird.
 */
public class DataBuffer
{
	private ByteArrayOutputStream buf = new ByteArrayOutputStream();
	private Deque<Integer> newlines = new LinkedList<>();
	
	/**
	 * Fügt die gelesenen Bytes dem Buffer hinzu.
	 */
	public void add (byte read[])
	{
		for (byte b : read)
			add(b);
	}
	
	/**
	 * Fügt das gelesene Byte dem Buffer hinzu.
	 */
	public void add (byte b)
	{
		if (b == 0xa)
			newlines.add(1 + buf.size() - (newlines.isEmpty() ? 0 : newlines.getLast()));
		buf.write(b);
	}
	
	/**
	 * Liest die ersten length Bytes und gibt diese zurück.
	 */
	public byte[] read (int length)
	{
		if (buf.size() < length)
			return null;
		byte b[] = new byte[length];
		System.arraycopy(buf.toByteArray(), 0, b, 0, length);
		byte buffer[] = new byte[buf.size() - length];
		System.arraycopy(buf.toByteArray(), length, buffer, 0, buffer.length);
		buf.reset();
		buf.write(buffer, 0, buffer.length);
		return b;
	}
	
	/**
	 * Liest eine Zeile und gibt diese zurück.
	 */
	public byte[] readLine ()
	{
		if (newlines.isEmpty())
			return null;
		return read(newlines.pollFirst());
	}
	
	/**
	 * Liest alle Bytes und gibt diese zurück.
	 */
	public byte[] readAll ()
	{
		byte b[] = buf.toByteArray();
		buf.reset();
		return b;
	}
}

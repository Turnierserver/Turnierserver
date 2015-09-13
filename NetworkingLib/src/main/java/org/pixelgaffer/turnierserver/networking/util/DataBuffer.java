/*
 * DataBuffer.java
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
package org.pixelgaffer.turnierserver.networking.util;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Diese Klasse ist ein Buffer für ein byte[], der von Paket-basierten Clients
 * genutzt wird.
 */
public class DataBuffer
{
	private ByteArrayOutputStream buf = new ByteArrayOutputStream();
	private LinkedList<Integer> newlines = new LinkedList<>();
	
	/**
	 * Fügt die gelesenen Bytes dem Buffer hinzu.
	 */
	public void add (byte read[], int off, int len)
	{
		for (int i = off; i < off + len; i++)
			add(read[i]);
	}
	
	/**
	 * Fügt die gelesenen Bytes dem Buffer hinzu.
	 */
	public void add (byte read[])
	{
		add(read, 0, read.length);
	}
	
	/**
	 * Fügt das gelesene Byte dem Buffer hinzu.
	 */
	public void add (byte b)
	{
		if (b == 0xa)
			newlines.add(1 + buf.size() - (newlines.isEmpty() ? 0 : newlines.stream().collect(Collectors.summingInt((i) -> i))));
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

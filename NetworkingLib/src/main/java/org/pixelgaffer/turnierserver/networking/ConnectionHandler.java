/*
 * ConnectionHandler.java
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
package org.pixelgaffer.turnierserver.networking;

import java.io.OutputStream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import naga.NIOSocket;
import naga.SocketObserver;

/**
 * Ein abstrakter ConnectionHandler, der einige nützliche Funktionen
 * implementiert.
 */
public abstract class ConnectionHandler extends OutputStream implements SocketObserver
{
	/** Gibt an, ob der Handler mit dem Socket connected ist. */
	@Getter
	private boolean connected = false;
	
	/** Der ConnectionPool, in dem dieser Handler ist. */
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private ConnectionPool<?> pool;
	
	/** Der zugrundeliegende Client. */
	@Getter(AccessLevel.PROTECTED)
	private NIOSocket client;
	
	/**
	 * Erstellt einen ConnectionHandler für das angegebene Socket.
	 */
	public ConnectionHandler (NIOSocket socket)
	{
		client = socket;
		client.listen(this);
	}
	
	@Override
	public void connectionOpened (NIOSocket socket)
	{
		connected = true;
		connected();
	}
	
	/**
	 * Wird aufgerufen, wenn die Connection zum Client aufgebaut wurde.
	 */
	protected void connected ()
	{
	}
	
	@Override
	public final void connectionBroken (NIOSocket socket, Exception exception)
	{
		connected = false;
		pool.remove(this);
		disconnected();
	}
	
	/**
	 * Disconnected den Client und entfernt den Handler aus dem
	 * {@link ConnectionPool} des Servers.
	 */
	public void disconnect ()
	{
		connected = false;
		pool.remove(this);
		client.close();
		disconnected();
	}
	
	/**
	 * Wird aufgerufen, wenn sich der Client disconnected hat oder disconnected
	 * wurde.
	 */
	protected void disconnected ()
	{
	}
	
	@Override
	public void packetSent (NIOSocket socket, Object tag)
	{
	}
	
	// wrap OutputStream
	
	@Override
	public void write (int b)
	{
		write(new byte[] { (byte)b });
	}
	
	@Override
	public void write (byte b[])
	{
		client.write(b);
	}
}

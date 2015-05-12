package org.pixelgaffer.turnierserver.networking;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import naga.NIOSocket;
import naga.SocketObserver;

/**
 * Ein abstraḱter ConnectionHandler, der einige nützliche Funktionen
 * implementiert.
 */
public abstract class ConnectionHandler implements SocketObserver
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
}
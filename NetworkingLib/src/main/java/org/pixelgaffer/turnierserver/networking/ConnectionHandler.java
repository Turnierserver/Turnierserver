package org.pixelgaffer.turnierserver.networking;

import java.io.IOException;
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
	public void write (int b) throws IOException
	{
		write(new byte[] { (byte)b });
	}
	
	@Override
	public void write (byte b[]) throws IOException
	{
		super.write(b);
	}
	
	@Override
	public void write (byte b[], int off, int len) throws IOException
	{
		super.write(b, off, len);
	}
	
	@Override
	public void flush () throws IOException
	{
		super.flush();
	}
}

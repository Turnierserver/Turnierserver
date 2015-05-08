package org.pixelgaffer.turnierserver.backend.server;

import java.io.IOException;

import lombok.Getter;
import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.NIOSocket;
import naga.ServerSocketObserver;

import org.pixelgaffer.turnierserver.backend.BackendMain;

/**
 * Diese Klasse öffnet einen Server, auf dem sich alle zur Verfügung stehenden
 * Worker melden. Dieser Server dient nicht dazu, dass sich dort die KIs melden!
 * Der Server für die KIs läuft auf dem Worker.
 */
public class BackendServer implements ServerSocketObserver
{
	/** Der Standart-Port für diesen Server. */
	public static final int DEFAULT_PORT = 1332;
	
	private NIOServerSocket server;
	@Getter
	private BackendConnectionPool pool = new BackendConnectionPool();
	
	/**
	 * Öffnet den Server auf dem angegebenen Port.
	 */
	public BackendServer (int port) throws IOException
	{
		server = BackendMain.getNioService().openServerSocket(port);
		server.listen(this);
		server.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		BackendMain.getLogger().info("BackendServer opened successfully on port " + port);
	}
	
	/**
	 * Öffnet den Server auf dem angegebenen Port mit der maximalen Anzahl an
	 * Clients.
	 */
	public BackendServer (int port, int maxClients) throws IOException
	{
		this(port);
		pool.setMaxConnections(maxClients);
	}
	
	@Override
	public void acceptFailed (IOException exception)
	{
		BackendMain.getLogger().warning("BackendServer: accept failed: " + exception);
	}
	
	@Override
	public void serverSocketDied (Exception exception)
	{
		BackendMain.getLogger().warning("BackendServer: socket died: " + exception);
	}
	
	@Override
	public void newConnection (NIOSocket nioSocket)
	{
		pool.add(nioSocket);
	}
}

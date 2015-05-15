package org.pixelgaffer.turnierserver.backend.server;

import java.io.IOException;

import naga.NIOSocket;

import org.pixelgaffer.turnierserver.backend.BackendMain;
import org.pixelgaffer.turnierserver.networking.Server;

/**
 * Diese Klasse öffnet einen Server, auf dem sich alle zur Verfügung stehenden
 * Worker melden. Dieser Server dient nicht dazu, dass sich dort die KIs melden!
 * Der Server für die KIs läuft auf dem Worker.
 */
public class BackendWorkerServer extends Server<BackendWorkerConnectionPool>
{
	/** Der Standart-Port für diesen Server. */
	public static final int DEFAULT_PORT = 1332;
	
	/**
	 * Öffnet den Server auf dem angegebenen Port.
	 */
	public BackendWorkerServer (int port) throws IOException
	{
		super(port, new BackendWorkerConnectionPool());
		BackendMain.getLogger().info("BackendServer opened successfully on port " + port);
	}
	
	/**
	 * Öffnet den Server auf dem angegebenen Port mit der maximalen Anzahl an
	 * Clients.
	 */
	public BackendWorkerServer (int port, int maxClients) throws IOException
	{
		super(port, new BackendWorkerConnectionPool(), maxClients);
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

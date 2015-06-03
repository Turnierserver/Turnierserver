package org.pixelgaffer.turnierserver.worker.server;

import java.io.IOException;

import naga.NIOSocket;

import org.pixelgaffer.turnierserver.networking.Server;

/**
 * Dieser Server spiegelt den FTP-Server auf dem Datastore für die Sandboxen,
 * die aus Sicherheitsgründen nur mit dem Worker kommunizieren dürfen.
 */
public class MirrorServer extends Server<MirrorConnectionPool>
{
	public static final int DEFAULT_PORT = 1338;
	
	/**
	 * Öffnet den Server auf dem angegebenen Port.
	 */
	public MirrorServer (int port) throws IOException
	{
		super(port, new MirrorConnectionPool());
	}
	
	/**
	 * Öffnet den Server auf dem angegebenen Port mit der maximalen Anzahl an
	 * Clients.
	 */
	public MirrorServer (int port, int maxClients) throws IOException
	{
		super(port, new MirrorConnectionPool(), maxClients);
	}

	public void newConnection (NIOSocket socket)
	{
		System.out.println("mirrorserver: " + socket);
		getPool().add(socket);
	}
}

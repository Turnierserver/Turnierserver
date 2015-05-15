package org.pixelgaffer.turnierserver.worker.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import naga.NIOSocket;

import org.pixelgaffer.turnierserver.networking.Server;

/**
 * Der Server des Workers, zu dem sich Sandbox-Rechner, Backend, und die KIs
 * verbinden.
 */
public class WorkerServer extends Server<WorkerConnectionPool>
{
	/** Die Connection vom Backend. */
	public static WorkerConnectionHandler backendConnection;
	
	/** Die Verbindungen mit den KIs, identifiziert mit der UUID der KI. */
	public static final Map<UUID, WorkerConnectionHandler> aiConnections = new HashMap<>();
	
	/** Der Standart-Port für diesen Server. */
	public static final int DEFAULT_PORT = 1337;
	
	/**
	 * Öffnet den Server auf dem angegebenen Port.
	 */
	public WorkerServer (int port) throws IOException
	{
		super(port, new WorkerConnectionPool());
	}
	
	/**
	 * Öffnet den Server auf dem angegebenen Port mit der maximalen Anzahl an
	 * Clients.
	 */
	public WorkerServer (int port, int maxClients) throws IOException
	{
		super(port, new WorkerConnectionPool(), maxClients);
	}
	
	@Override
	public void newConnection (NIOSocket socket)
	{
		pool.add(socket);
	}
}

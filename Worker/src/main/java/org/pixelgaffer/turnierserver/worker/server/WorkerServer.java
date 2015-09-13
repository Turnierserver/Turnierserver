/*
 * WorkerServer.java
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

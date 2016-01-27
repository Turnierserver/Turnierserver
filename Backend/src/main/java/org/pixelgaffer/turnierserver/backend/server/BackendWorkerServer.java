/*
 * BackendWorkerServer.java
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
		BackendMain.getLogger().info("BackendServer opened successfully on port " + port);
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

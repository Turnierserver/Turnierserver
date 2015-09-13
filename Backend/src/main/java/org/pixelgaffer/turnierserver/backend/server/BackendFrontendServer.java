/*
 * BackendFrontendServer.java
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
 * Diese Klasse öffnet einen Server, auf dem sich das Frontend verbindet.
 */
public class BackendFrontendServer extends Server<BackendFrontendConnectionPool>
{
	/** Der Standart-Port für diesen Server. */
	public static final int DEFAULT_PORT = 1333;
	
	/**
	 * Öffnet den Server auf dem angegebenen Port.
	 */
	public BackendFrontendServer (int port) throws IOException
	{
		super(port, new BackendFrontendConnectionPool());
		BackendMain.getLogger().info("Server opened successfully on port " + port);
	}
	
	@Override
	public void acceptFailed (IOException exception)
	{
		BackendMain.getLogger().warning("Accept failed: " + exception);
	}
	
	@Override
	public void serverSocketDied (Exception exception)
	{
		BackendMain.getLogger().warning("Socket died: " + exception);
	}
	
	@Override
	public void newConnection (NIOSocket nioSocket)
	{
		pool.add(nioSocket);
	}
}

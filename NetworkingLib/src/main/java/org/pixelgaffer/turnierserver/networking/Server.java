/*
 * Server.java
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

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import naga.ConnectionAcceptor;
import naga.NIOServerSocket;
import naga.ServerSocketObserver;

public abstract class Server <P extends ConnectionPool<? extends ConnectionHandler>> implements ServerSocketObserver
{
	private NIOServerSocket server;
	
	@Getter
	protected P pool;
	
	/**
	 * Öffnet den Server auf dem angegebenen Port.
	 */
	public Server (int port, @NonNull P pool) throws IOException
	{
		this.pool = pool;
		server = NetworkService.getService().openServerSocket(port);
		server.listen(this);
	}
	
	/**
	 * Öffnet den Server auf dem angegebenen Port mit der maximalen Anzahl an
	 * Clients.
	 */
	public Server (int port, P pool, int maxClients) throws IOException
	{
		this(port, pool);
		pool.setMaxConnections(maxClients);
	}

	public void setConnectionAcceptor (ConnectionAcceptor acceptor)
	{
		server.setConnectionAcceptor(acceptor);
	}
	
	@Override
	public void acceptFailed (IOException exception)
	{
	}

	@Override
	public void serverSocketDied (Exception exception)
	{
	}
}

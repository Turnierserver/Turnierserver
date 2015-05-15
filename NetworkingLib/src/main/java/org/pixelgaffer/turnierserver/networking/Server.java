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

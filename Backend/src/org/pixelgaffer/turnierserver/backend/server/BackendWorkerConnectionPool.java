package org.pixelgaffer.turnierserver.backend.server;

import lombok.NoArgsConstructor;
import naga.NIOSocket;

import org.pixelgaffer.turnierserver.networking.ConnectionPool;

@NoArgsConstructor
public class BackendWorkerConnectionPool extends ConnectionPool<BackendWorkerConnectionHandler>
{
	public void add (NIOSocket client)
	{
		add(new BackendWorkerConnectionHandler(client));
	}
}

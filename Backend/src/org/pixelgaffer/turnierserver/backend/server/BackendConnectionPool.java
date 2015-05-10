package org.pixelgaffer.turnierserver.backend.server;

import lombok.NoArgsConstructor;
import naga.NIOSocket;

import org.pixelgaffer.turnierserver.networking.ConnectionPool;

@NoArgsConstructor
public class BackendConnectionPool extends ConnectionPool<BackendConnectionHandler>
{
	public void add (NIOSocket client)
	{
		add(new BackendConnectionHandler(client));
	}
}

package org.pixelgaffer.turnierserver.worker.server;

import naga.NIOSocket;

import org.pixelgaffer.turnierserver.networking.ConnectionPool;

public class MirrorConnectionPool extends ConnectionPool<MirrorConnectionHandler>
{
	public boolean add (NIOSocket socket)
	{
		return add(new MirrorConnectionHandler(socket));
	}
}

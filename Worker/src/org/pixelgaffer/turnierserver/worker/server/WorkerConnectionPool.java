package org.pixelgaffer.turnierserver.worker.server;

import naga.NIOSocket;

import org.pixelgaffer.turnierserver.networking.ConnectionPool;

public class WorkerConnectionPool extends ConnectionPool<WorkerConnectionHandler>
{
	public boolean add (NIOSocket socket)
	{
		return add(new WorkerConnectionHandler(socket));
	}
}

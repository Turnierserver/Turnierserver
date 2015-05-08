package org.pixelgaffer.turnierserver.backend.server;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import naga.NIOSocket;

import org.pixelgaffer.turnierserver.backend.BackendMain;

@NoArgsConstructor
public class BackendConnectionPool implements Runnable
{
	@Getter
	@Setter
	private int maxConnections = -1;
	
	private List<BackendConnectionHandler> connections = new ArrayList<>();
	private Deque<BackendConnectionHandler> queue = new ArrayDeque<>();
	
	public void add (BackendConnectionHandler handler)
	{
		synchronized (connections)
		{
			connections.add(handler);
		}
	}
	
	public void add (NIOSocket client)
	{
		add(new BackendConnectionHandler(client));
	}
	
	@Override
	public void run ()
	{
		try
		{
			while (true)
			{
				BackendMain.getNioService().selectBlocking();
			}
		}
		catch (IOException ioe)
		{
			BackendMain.getLogger().info("BackendConnectionPool: Thread stopped: " + ioe);
		}
	}
	
	public void start ()
	{
		new Thread(this, "ConnectionPool").start();
	}
}

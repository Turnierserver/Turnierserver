package org.pixelgaffer.turnierserver.backend.server;

import java.io.IOException;
import java.net.ServerSocket;

public class BackendServer
{
	private ServerSocket server;
	
	public BackendServer (int port) throws IOException
	{
		server = new ServerSocket(port);
	}
}

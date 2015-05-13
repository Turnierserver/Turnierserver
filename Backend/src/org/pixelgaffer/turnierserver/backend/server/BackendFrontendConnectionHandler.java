package org.pixelgaffer.turnierserver.backend.server;

import naga.NIOSocket;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

public class BackendFrontendConnectionHandler extends ConnectionHandler
{
	/** Der lokale Buffer mit den noch nicht gelesenen bytes. */
	private DataBuffer buffer = new DataBuffer();
	
	public BackendFrontendConnectionHandler (NIOSocket socket)
	{
		super(socket);
	}
	
	@Override
	public void packetReceived (NIOSocket socket, byte[] packet)
	{
		buffer.add(packet);
		byte line[];
		while ((line = buffer.readLine()) != null)
		{
			try
			{
				BackendFrontendCommand cmd = Parsers.getParser(false).parse(line, BackendFrontendCommand.class);
				System.out.println(cmd);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

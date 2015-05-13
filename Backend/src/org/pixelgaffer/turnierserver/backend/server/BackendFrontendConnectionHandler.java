package org.pixelgaffer.turnierserver.backend.server;

import naga.NIOSocket;

import org.msgpack.MessagePack;
import org.pixelgaffer.turnierserver.backend.BackendMain;
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
				@SuppressWarnings("deprecation")
				BackendFrontendCommand cmd = MessagePack.unpack(line, BackendFrontendCommand.class);
				System.out.println(cmd);
			}
			catch (Exception e)
			{
				BackendMain.getLogger().severe("BackendConnectionHandler: while creating WorkerConnection: " + e);
			}
		}
	}
}

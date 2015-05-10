package org.pixelgaffer.turnierserver.backend.server;

import lombok.Getter;
import naga.NIOSocket;

import org.msgpack.MessagePack;
import org.pixelgaffer.turnierserver.backend.BackendMain;
import org.pixelgaffer.turnierserver.backend.WorkerConnection;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

/**
 * Diese Klasse ist der ConnectionHandler für den BackendServer.
 */
public class BackendConnectionHandler extends ConnectionHandler
{
	@Getter
	private boolean connected = false;
	
	/** Der lokale Buffer mit den noch nicht gelesenen bytes. */
	private DataBuffer buffer = new DataBuffer();
	
	/** Die erstellte WorkerConnection. */
	private WorkerConnection workerConnection;
	
	
	public BackendConnectionHandler (NIOSocket socket)
	{
		super(socket);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void packetReceived (NIOSocket socket, byte[] packet)
	{
		buffer.add(packet);
		byte line[];
		while ((line = buffer.readLine()) != null)
		{
			if (workerConnection == null)
			{
				try
				{
					workerConnection = new WorkerConnection(this, socket.getAddress(), MessagePack.unpack(line,
							WorkerInfo.class));
				}
				catch (Exception e)
				{
					BackendMain.getLogger().severe("BackendConnectionHandler: while creating WorkerConnection: " + e);
				}
			}
		}
	}
}

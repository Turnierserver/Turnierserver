package org.pixelgaffer.turnierserver.backend.server;

import naga.NIOSocket;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.BackendMain;
import org.pixelgaffer.turnierserver.backend.WorkerConnection;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

/**
 * Diese Klasse ist der ConnectionHandler für den BackendServer.
 */
public class BackendWorkerConnectionHandler extends ConnectionHandler
{
	/** Der lokale Buffer mit den noch nicht gelesenen bytes. */
	private DataBuffer buffer = new DataBuffer();
	
	/** Die erstellte WorkerConnection. */
	private WorkerConnection workerConnection;
	
	
	public BackendWorkerConnectionHandler (NIOSocket socket)
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
			if (workerConnection == null)
			{
				try
				{
					workerConnection = new WorkerConnection(this, socket.getAddress(),
							Parsers.getWorker().parse(line, WorkerInfo.class));
				}
				catch (Exception e)
				{
					BackendMain.getLogger().severe("BackendConnectionHandler: while creating WorkerConnection: " + e);
				}
			}
		}
	}
}

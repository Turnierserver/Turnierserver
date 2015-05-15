package org.pixelgaffer.turnierserver.backend.server;

import naga.NIOSocket;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.BackendMain;
import org.pixelgaffer.turnierserver.backend.Workers;
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
				BackendFrontendCommand cmd = Parsers.getFrontend().parse(line, BackendFrontendCommand.class);
				if (cmd.getAction().equals("compile"))
				{
					Workers.getAvailableWorker().compile(cmd.getId(), cmd.getGametype());
				}
				else if (cmd.getAction().equals("start"))
				{
					System.out.println(cmd);
				}
				else
					BackendMain.getLogger().severe(
							"Unknown action from Frontend (" + socket.getIp() + "): " + cmd.getAction());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

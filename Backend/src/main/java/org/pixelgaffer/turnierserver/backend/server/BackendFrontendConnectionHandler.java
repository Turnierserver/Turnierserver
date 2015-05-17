package org.pixelgaffer.turnierserver.backend.server;

import lombok.Getter;
import naga.NIOSocket;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.BackendMain;
import org.pixelgaffer.turnierserver.backend.Games;
import org.pixelgaffer.turnierserver.backend.Workers;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

public class BackendFrontendConnectionHandler extends ConnectionHandler
{
	@Getter
	private static BackendFrontendConnectionHandler frontend;
	
	/** Der lokale Buffer mit den noch nicht gelesenen bytes. */
	private DataBuffer buffer = new DataBuffer();
	
	public BackendFrontendConnectionHandler (NIOSocket socket)
	{
		super(socket);
	}
	
	@Override
	protected void connected ()
	{
		BackendMain.getLogger().info("BackendFrontendConnectionHandler: Frontend (" + getClient().getIp() + ") connected.");
		frontend = this;
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
					Games.startGame(cmd.getGametype(), cmd.getAis());
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
	
	public void sendMessage (byte message[])
	{
		throw new UnsupportedOperationException();
	}
}

package org.pixelgaffer.turnierserver.backend.server;

import static java.nio.charset.StandardCharsets.UTF_8;
import naga.NIOSocket;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.BackendMain;
import org.pixelgaffer.turnierserver.backend.Jobs;
import org.pixelgaffer.turnierserver.backend.server.message.BackendFrontendCommand;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

public class BackendFrontendConnectionHandler extends ConnectionHandler
{
	private static BackendFrontendConnectionHandler frontend;
	
	public static BackendFrontendConnectionHandler getFrontend ()
	{
		while (true)
		{
			if (frontend != null && frontend.isConnected())
				return frontend;
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/** Der lokale Buffer mit den noch nicht gelesenen bytes. */
	private DataBuffer buffer = new DataBuffer();
	
	public BackendFrontendConnectionHandler (NIOSocket socket)
	{
		super(socket);
	}
	
	@Override
	protected void connected ()
	{
		BackendMain.getLogger().info(
				"BackendFrontendConnectionHandler: Frontend (" + getClient().getIp() + ") connected.");
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
				BackendMain.getLogger().info("BackendFrontendConnectionHandler: Empfangen: " + cmd);
				Jobs.processCommand(cmd);
			}
			catch (Exception e)
			{
				BackendMain.getLogger().critical("Failed to parse command from frontend: " + e);
			}
		}
	}
	
	public synchronized void sendMessage (byte message[])
	{
		getClient().write(message);
		getClient().write("\n".getBytes(UTF_8));
	}
}

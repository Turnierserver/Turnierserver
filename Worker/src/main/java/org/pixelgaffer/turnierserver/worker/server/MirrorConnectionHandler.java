package org.pixelgaffer.turnierserver.worker.server;

import static java.nio.charset.StandardCharsets.UTF_8;
import naga.NIOSocket;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;
import org.pixelgaffer.turnierserver.worker.WorkerMain;

public class MirrorConnectionHandler extends ConnectionHandler
{
	private DataBuffer buffer = new DataBuffer();
	
	public MirrorConnectionHandler (NIOSocket socket)
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
				MirrorRequest req = Parsers.getSandbox().parse(line, MirrorRequest.class);
				System.out.println("MirrorConnectionHandler:32: " + req);
				// IO in neuem Thread wegen blockieren und so
				new Thread( () -> {
					try
					{
						write((DatastoreFtpClient.aiSize(req.getId(), req.getVersion()) + "\n").getBytes(UTF_8));
						DatastoreFtpClient.retrieveAi(req.getId(), req.getVersion(), MirrorConnectionHandler.this);
					}
					catch (Exception e)
					{
						try
						{
							write("0\n".getBytes(UTF_8));
						}
						catch (Exception e1)
						{
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}).start();
			}
			catch (Exception e)
			{
				WorkerMain.getLogger().severe("MirrorConnectionHandler: Failed to parse request: " + e);
			}
		}
	}
}

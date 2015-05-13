package org.pixelgaffer.turnierserver.worker.backendclient;

import java.io.IOException;

import lombok.Getter;
import naga.NIOSocket;
import naga.SocketObserver;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.networking.NetworkService;
import org.pixelgaffer.turnierserver.networking.messages.StartAi;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;
import org.pixelgaffer.turnierserver.worker.WorkerMain;

/**
 * Diese Klasse ist der Client zum Backend.
 */
public class BackendClient implements SocketObserver
{
	private NIOSocket client;
	
	/** Speichert, ob der Client verbunden ist. */
	@Getter
	private boolean connected = false;
	
	private DataBuffer buf = new DataBuffer();
	
	public BackendClient (String ip, int port) throws IOException
	{
		client = NetworkService.getService().openSocket(ip, port);
		client.listen(this);
	}
	
	@Override
	public void connectionOpened (NIOSocket socket)
	{
		WorkerMain.getLogger().info("BackendClient: Established Connection to " + socket.getIp());
		connected = true;
	}
	
	@Override
	public void connectionBroken (NIOSocket socket, Exception exception)
	{
		WorkerMain.getLogger().severe("BackendClient: Connection to Backend broken: " + exception);
		connected = false;
	}
	
	@Override
	public void packetReceived (NIOSocket socket, byte[] packet)
	{
		buf.add(packet);
		byte line[];
		while ((line = buf.readLine()) != null)
		{
			try
			{
				// der einzige Befehl des Backends über diese Verbindung ist
				// "starte KI sowieso mit UUID sowieso"
				StartAi sai = Parsers.getParser(false).parse(line, StartAi.class);
				System.out.println(sai);
			}
			catch (Exception e)
			{
				WorkerMain.getLogger().severe("BackendClient: Failed to parse Command: " + e);
			}
		}
	}
	
	@Override
	public void packetSent (NIOSocket socket, Object tag)
	{
	}
}

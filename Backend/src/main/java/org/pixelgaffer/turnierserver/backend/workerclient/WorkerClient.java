package org.pixelgaffer.turnierserver.backend.workerclient;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import lombok.Getter;
import naga.NIOSocket;
import naga.SocketObserver;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.BackendMain;
import org.pixelgaffer.turnierserver.backend.Games;
import org.pixelgaffer.turnierserver.networking.NetworkService;
import org.pixelgaffer.turnierserver.networking.messages.MessageForward;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

/**
 * Diese Klasse dient zur Verbindung mit einem Worker.
 */
public class WorkerClient implements SocketObserver
{
	/** Der Client zum Worker. */
	private NIOSocket client;
	
	@Getter
	private boolean connected;
	
	private DataBuffer buffer = new DataBuffer();
	
	/**
	 * Öffnet den Client zum Worker mit der IP addr und dem Port aus info.
	 */
	public WorkerClient (String addr, WorkerInfo info) throws IOException
	{
		client = NetworkService.getService().openSocket(addr, info.getPort());
		client.listen(this);
	}
	
	/**
	 * Sendet den MessageForward an den Worker.
	 */
	public void sendMessage (MessageForward mf) throws IOException
	{
		client.write(Parsers.getWorker().parse(mf));
		client.write("\n".getBytes(UTF_8));
	}
	
	/**
	 * Schließt die Verbindung.
	 */
	public void disconnect ()
	{
		client.close();
		connected = false;
	}
	
	@Override
	public void connectionOpened (NIOSocket socket)
	{
		BackendMain.getLogger().info("Established connection to " + socket.getIp());
		connected = true;
		socket.write("B\n".getBytes(UTF_8));
	}
	
	@Override
	public void connectionBroken (NIOSocket socket, Exception exception)
	{
		connected = false;
		BackendMain.getLogger().warning("Connection closed"
				+ (exception == null ? "" : ": " + exception));
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
				MessageForward mf = Parsers.getWorker().parse(line, MessageForward.class);
				Games.receiveMessage(mf);
			}
			catch (IOException e)
			{
				BackendMain.getLogger().critical("Failed to parse line: " + e);
			}
		}
	}
	
	@Override
	public void packetSent (NIOSocket socket, Object tag)
	{
	}
}

package org.pixelgaffer.turnierserver.backend.workerclient;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import lombok.Getter;
import naga.NIOSocket;
import naga.SocketObserver;

import org.pixelgaffer.turnierserver.networking.NetworkService;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;

/**
 * Diese Klasse dient zur Verbindung mit einem Worker.
 */
public class WorkerClient implements SocketObserver
{
	private NIOSocket client;
	
	@Getter
	private boolean connected;
	
	public WorkerClient (String addr, WorkerInfo info) throws IOException
	{
		client = NetworkService.getService().openSocket(addr, info.getPort());
	}
	
	/** Schließt die Verbindung. */
	public void disconnect ()
	{
		client.close();
		connected = false;
	}
	
	@Override
	public void connectionOpened (NIOSocket socket)
	{
		connected = true;
		socket.write("B\n".getBytes(UTF_8));
	}
	
	@Override
	public void connectionBroken (NIOSocket socket, Exception exception)
	{
		connected = false;
		exception.printStackTrace();
	}
	
	@Override
	public void packetReceived (NIOSocket socket, byte[] packet)
	{
		System.out.println("WorkerClient:46: " + new String(packet, UTF_8));
	}
	
	@Override
	public void packetSent (NIOSocket socket, Object tag)
	{
	}
}

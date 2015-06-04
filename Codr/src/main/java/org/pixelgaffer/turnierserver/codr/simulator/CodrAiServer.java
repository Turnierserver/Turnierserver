package org.pixelgaffer.turnierserver.codr.simulator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.NonNull;

import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;

/**
 * Diese Klasse ist ein Server für KIs und die Entsprechung für den Worker.
 */
public class CodrAiServer extends Thread
{
	private CodrGameImpl game;
	
	private ServerSocket server;
	
	public CodrAiServer (@NonNull CodrGameImpl gameImpl) throws IOException
	{
		super("CodrAiServer");
		game = gameImpl;
		
		server = new ServerSocket(0, 0, InetAddress.getLocalHost());
		// Port 0 heißt dass irgendein Port benutzt wird
	}
	
	@Override
	public void run ()
	{
		while (!server.isClosed())
		{
			try
			{
				Socket s = server.accept();
				if (!s.getInetAddress().equals(InetAddress.getLocalHost()))
				{
					ErrorLog.write(s.getInetAddress()
							+ " hat sich mit dem lokalen AiServer verbunden. Verbindung getötet.");
					s.close();
				}
				else
				{
					CodrAiServerConnectionHandler handler = new CodrAiServerConnectionHandler(game, s);
					handler.start();
				}
			}
			catch (Exception e)
			{
				ErrorLog.write("Fehler in der CodrAiServer-Loop: " + e);
				e.printStackTrace();
			}
		}
	}
	
	public int getPort ()
	{
		return server.getLocalPort();
	}
}

/*
 * CodrAiServer.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
		
		server = new ServerSocket(0, 0, InetAddress.getLoopbackAddress());
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
				if (!s.getInetAddress().equals(InetAddress.getLoopbackAddress()))
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

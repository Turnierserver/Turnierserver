/*
 * BackendFrontendConnectionHandler.java
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
package org.pixelgaffer.turnierserver.backend.server;

import static java.nio.charset.StandardCharsets.UTF_8;
import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.BackendMain;
import org.pixelgaffer.turnierserver.backend.Jobs;
import org.pixelgaffer.turnierserver.backend.server.message.BackendFrontendCommand;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;
import naga.NIOSocket;

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
		BackendMain.getLogger().info("Das Frontend (" + getClient().getIp() + ") hat sich verbunden");
		frontend = this;
	}
	
	@Override
	protected void disconnected ()
	{
		BackendMain.getLogger().info("Das Frontend hat die Verbindung getrennt");
		frontend = null;
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
				BackendMain.getLogger().info("Empfangen: " + cmd);
				Jobs.processCommand(cmd);
			}
			catch (Exception e)
			{
				BackendMain.getLogger().critical("Fehler beim Parsen des Befehls vom Frontend: " + e);
			}
		}
	}
	
	public synchronized void sendMessage (byte message[])
	{
		getClient().write(message);
		getClient().write("\n".getBytes(UTF_8));
	}
}

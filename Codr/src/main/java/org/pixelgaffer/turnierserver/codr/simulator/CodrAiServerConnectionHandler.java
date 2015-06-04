package org.pixelgaffer.turnierserver.codr.simulator;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CodrAiServerConnectionHandler extends Thread
{
	@NonNull
	private CodrGameImpl game;
	
	@NonNull
	private Socket client;
	
	private UUID uuid;
	
	@Override
	public void run ()
	{
		try
		{
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();
			DataBuffer buf = new DataBuffer();
			
			// die UUID lesen
			while (uuid == null)
			{
				byte r[] = new byte[8192];
				int read = in.read(r);
				buf.add(r, 0, read);
			}
			
		}
		catch (IOException ioe)
		{
		}
	}
}

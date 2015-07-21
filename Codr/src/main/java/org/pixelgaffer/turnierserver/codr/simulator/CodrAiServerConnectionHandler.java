package org.pixelgaffer.turnierserver.codr.simulator;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

@RequiredArgsConstructor
public class CodrAiServerConnectionHandler extends Thread
{
	@NonNull
	private CodrGameImpl game;
	
	@NonNull
	private Socket client;
	
	private UUID uuid;
	private CodrAiWrapper aiw;
	
	public void send (byte b[]) throws IOException
	{
		System.out.println("Sende an " + uuid + ": " + new String(b, UTF_8));
		client.getOutputStream().write(b);
		client.getOutputStream().write("\n".getBytes(UTF_8));
		client.getOutputStream().flush();
	}
	
	public void close () throws IOException
	{
		client.close();
	}
	
	@Override
	public void run ()
	{
		try
		{
			InputStream in = client.getInputStream();
			DataBuffer buf = new DataBuffer();
			
			// die UUID lesen
			while (uuid == null)
			{
				byte r[] = new byte[8192];
				int read = in.read(r);
				if (read < 0)
					throw new EOFException();
				buf.add(r, 0, read);
				
				r = buf.readLine();
				if (r != null)
					uuid = UUID.fromString(new String(r, UTF_8).trim());
			}
			aiw = game.getAi(uuid);
			aiw.connected(this);
			
			// den Rest an die Spiellogik weiterleiten
			while (true)
			{
				byte line[];
				while ((line = buf.readLine()) != null)
					aiw.receiveMessage(line);
				
				line = new byte[8192];
				int read = in.read(line);
				if (read < 0)
					throw new EOFException();
				System.out.println("Habe " + read + " Bytes empfangen");
				buf.add(line, 0, read);
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}

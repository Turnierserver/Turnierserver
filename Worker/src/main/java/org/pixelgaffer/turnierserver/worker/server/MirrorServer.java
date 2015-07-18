package org.pixelgaffer.turnierserver.worker.server;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.PropertyUtils.*;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import org.pixelgaffer.turnierserver.Logger;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import org.pixelgaffer.turnierserver.worker.WorkerMain;

/**
 * Dieser Server spiegelt den FTP-Server auf dem Datastore für die Sandboxen,
 * die aus Sicherheitsgründen nur mit dem Worker kommunizieren dürfen.
 */
public class MirrorServer extends Thread
{
	public static final int DEFAULT_PORT = 1338;
	
	private ServerSocket server;
	
	/**
	 * Öffnet den Server auf dem angegebenen Port.
	 */
	public MirrorServer (int port) throws IOException
	{
		server = new ServerSocket(port);
	}
	
	@Override
	public void run ()
	{
		while (!server.isClosed())
		{
			try
			{
				Socket client = server.accept();
				WorkerMain.getLogger().info("MirrorServer: " + client + " hat sich verbunden");
				new Thread( () -> {
					try
					{
						BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
						
						String line = in.readLine();
						if (line == null)
							throw new EOFException();
						int id = Integer.valueOf(line);
						
						line = in.readLine();
						if (line == null)
							throw new EOFException();
						int version = Integer.valueOf(line);
						
						OutputStream out = client.getOutputStream();
						
						byte[] salt = generateSalt();
						out.write(salt);
						byte[] hash = sha256(salt);
						line = in.readLine();
						if (line == null)
							throw new EOFException();
						if (!Arrays.equals(Base64.getDecoder().decode(line), hash))
						{
							WorkerMain.getLogger().critical("Der Mirror-Klient " + client + " hat das falsche Passwort gesendet!");
							return;
						}
						
						out.write((Long.toString(DatastoreFtpClient.aiSize(id, version)) + "\n").getBytes(UTF_8));
						DatastoreFtpClient.retrieveAi(id, version, out);
					}
					catch (EOFException eofe)
					{
						WorkerMain.getLogger().warning("Der Client hat sich während der Übertragung disconnected");
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					finally
					{
						try
						{
							client.close();
						}
						catch (IOException ioe)
						{
						}
					}
				}).start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static final Random r = new SecureRandom();
	
	private static byte[] generateSalt ()
	{
		byte[] salt = new byte[getIntRequired(WORKER_MIRROR_SALT_LENGTH)];
		r.nextBytes(salt);
		return salt;
	}
	
	private static byte[] sha256 (byte[] salt) throws NoSuchAlgorithmException
	{
		byte[] hash = getStringRequired(WORKER_MIRROR_PASSWORD).getBytes();
		for (int i = 0; i < getIntRequired(WORKER_MIRROR_PASSWORD_REPEATS); i++)
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			md.update(hash);
			hash = md.digest();
		}
		return hash;
	}
}

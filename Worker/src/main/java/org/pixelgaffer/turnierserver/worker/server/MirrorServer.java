package org.pixelgaffer.turnierserver.worker.server;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.PropertyUtils.WORKER_MIRROR_PASSWORD;
import static org.pixelgaffer.turnierserver.PropertyUtils.WORKER_MIRROR_PASSWORD_REPEATS;
import static org.pixelgaffer.turnierserver.PropertyUtils.WORKER_MIRROR_SALT_LENGTH;
import static org.pixelgaffer.turnierserver.PropertyUtils.getIntRequired;
import static org.pixelgaffer.turnierserver.PropertyUtils.getStringRequired;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
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
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import org.pixelgaffer.turnierserver.worker.LibraryCache;
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
				WorkerMain.getLogger().info(client.getInetAddress().getHostName() + " hat sich verbunden");
				new Thread( () -> {
					try
					{
						BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
						
						boolean ai = true;
						int id = 0, version = 0; // ai
						String name = "nonexistent-library", language = "nonexistent-language"; // lib
						
						String line = in.readLine();
						if (line == null)
							throw new EOFException();
						try
						{
							id = Integer.valueOf(line);
						}
						catch (NumberFormatException nfe)
						{
							ai = false;
							name = line;
						}
						
						if (ai)
						{
							line = in.readLine();
							if (line == null)
								throw new EOFException();
							version = Integer.valueOf(line);
						}
						else
						{
							language = in.readLine();
							if (language == null)
								throw new EOFException();
						}
						
						OutputStream out = client.getOutputStream();
						
						byte[] salt = generateSalt();
						out.write(salt);
						byte[] hash = sha256(salt);
						line = in.readLine();
						if (line == null)
							throw new EOFException();
						if (!Arrays.equals(Base64.getDecoder().decode(line), hash))
						{
							WorkerMain.getLogger()
									.critical("Der Mirror-Klient " + client + " hat das falsche Passwort gesendet!");
							return;
						}
						
						if (ai)
						{
							out.write((Long.toString(DatastoreFtpClient.aiSize(id, version)) + "\n").getBytes(UTF_8));
							DatastoreFtpClient.retrieveAi(id, version, out);
						}
						else
						{
							File tar = LibraryCache.getCache().getLibTarBz2(language, name);
							out.write((Long.toString(tar.length()) + "\n").getBytes(UTF_8));
							FileInputStream fin = new FileInputStream(tar);
							byte buf[] = new byte[8192]; int read;
							while ((read = fin.read(buf)) > 0)
								out.write(buf, 0, read);
							fin.close();
						}
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

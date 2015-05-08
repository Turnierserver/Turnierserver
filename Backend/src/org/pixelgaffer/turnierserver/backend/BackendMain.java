package org.pixelgaffer.turnierserver.backend;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;
import java.util.logging.Logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import naga.NIOService;

import org.pixelgaffer.turnierserver.backend.server.BackendServer;

/**
 * Diese Klasse startet das Backend und enthält einige für das ganze Programm
 * nützliche Funktionen.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BackendMain
{
	/** Der Naga NIOService. */
	@Getter
	private static NIOService nioService;
	static
	{
		try
		{
			nioService = new NIOService();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/** Gibt den Standartlogger zurück. */
	public static Logger getLogger ()
	{
		return Logger.getLogger("BackendServer");
	}
	
	/** Eine SecureRandom Instanz. */
	@Getter
	private static final SecureRandom secureRandom = new SecureRandom();
	
	/** Generiert einen Salt mit der angegebenen Größe. */
	public static byte[] generateSalt (int size)
	{
		byte salt[] = new byte[size];
		getSecureRandom().nextBytes(salt);
		return salt;
	}
	
	/**
	 * Gibt den n-ten SHA256-Hash der Eingabe zurück.
	 */
	@SneakyThrows
	public static byte[] sha256 (byte input[], byte salt[], int n)
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte hash[] = input;
		for (int i = 0; i < n; i++)
		{
			md.reset();
			if (salt != null)
				md.update(salt);
			md.update(hash);
			hash = md.digest();
		}
		return hash;
	}
	
	public static void main (String args[]) throws IOException
	{
		// Parse arguments
		if (args.length > 0)
		{
			if (args[0] == "--help")
			{
				
			}
			else if (args[0].equalsIgnoreCase("--genpw"))
			{
				System.out.println(Base64.getUrlEncoder().encodeToString(generateSalt(21)));
				return;
			}
		}
		
		// Properties laden
		Properties p = new Properties(System.getProperties());
		p.load(new FileInputStream(args.length > 0 ? args[0] : "/etc/turnierserver/backend/server.prop"));
		System.setProperties(p);
		
		// Server starten
		getLogger().info("BackendServer starting");
		BackendServer server = new BackendServer(
				Integer.parseInt(p.getProperty("turnierserver.backend.server.port",
						Integer.toString(BackendServer.DEFAULT_PORT))));
		server.getPool().start();
		getLogger().info("BackendServer started");
	}
}

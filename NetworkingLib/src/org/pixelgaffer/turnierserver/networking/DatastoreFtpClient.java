package org.pixelgaffer.turnierserver.networking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Diese Klasse verbindet sich zum FTP Server auf dem Datastore und enthält
 * Methoden zum Abrufen der Daten.
 * 
 * NICHT MULTITHREAD-SICHER
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatastoreFtpClient
{
	private static FTPClient client;
	
	/**
	 * Verbindet mit dem Server mithilfe der Systemproperties.
	 */
	private static void connect () throws IOException
	{
		if (client == null)
			client = new FTPClient();
		if (!client.isConnected())
		{
			// zum Server verbinden
			client.connect(System.getProperty("turnierserver.datastore.host"),
					Integer.parseInt(System.getProperty("turnierserver.datastore.port")));
			int reply = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply))
				throw new IOException("Server answered: " + reply);
			// beim Server anmelden
			System.out.println("logging in …");
			String username = System.getProperty("turnierserver.datastore.username");
			String password = System.getProperty("turnierserver.datastore.password");
			if (!client.login(username, password))
				throw new IOException("Failed to login: " + client.getReplyString());
			System.out.println("logged in");
			// passiven Modus benutzen (Firewall …)
			client.enterLocalPassiveMode();
		}
	}
	
	private static String aiPath (String user, String name)
	{
		return "Users/" + user + "/" + name + "/bin";
	}
	
	private static String aiPath (String user, String name, int version)
	{
		return "Users/" + user + "/" + name + "/v" + version;
	}
	
	/**
	 * Speichert den Remote-File im OutputStream local.
	 */
	public static void retrieveFile (String remote, OutputStream local) throws IOException
	{
		connect();
		if (!client.retrieveFile(remote, local))
			throw new IOException("Failed to retrieve file " + remote + ": " + client.getReplyString());
		local.close();
	}
	
	/**
	 * Speichert alles im Remote-Directory im local-Verzeichnis.
	 */
	public static void retrieveDir (String remote, File local) throws IOException
	{
		connect();
		System.out.println("retrieveDir: " + remote);
		String cwd = client.printWorkingDirectory();
		if (!client.changeWorkingDirectory(remote))
			throw new IOException("Failed to change working directory: " + client.getReplyString());
		local.mkdirs();
		for (FTPFile f : client.listFiles())
		{
			if (f.isFile())
				retrieveFile(f.getName(), new FileOutputStream(new File(local, f.getName())));
			else
				retrieveDir(f.getName(), new File(local, f.getName()));
		}
		client.cwd(cwd);
	}
	
	/**
	 * Speichert das tar-bzip2-Archiv mit den compilierten Daten der KI im
	 * OutputStream local.
	 */
	public static void retrieveAi (String user, String name, int version, OutputStream local) throws IOException
	{
		retrieveFile(aiPath(user, name) + "/v" + version + ".tar.bz2", local);
	}
	
	/**
	 * Speichert alle Dateien im Source-Folder der KI in einem temporären
	 * Verzeichnis und gibt dieses zurück.
	 */
	public static File retrieveAiSource (String user, String name, int version) throws IOException
	{
		File tmp = Files.createTempDirectory("ai").toFile();
		retrieveDir(aiPath(user, name, version), tmp);
		return tmp;
	}
	
	/**
	 * Speichert das jar-Archiv der Gamelogic im OutputStream local.
	 */
	public static void retrieveGameLogic (String game, OutputStream local) throws IOException
	{
		retrieveFile("Games/" + game + "/Logic.jar", local);
	}
	
	/**
	 * Speichert die jar-Archive der Bibliothek im Verzeichnis local.
	 */
	public static void retrieveLibrary (String lib, String language, File local) throws IOException
	{
		retrieveDir("Libraries/" + language + "/" + lib, local);
	}
	
	/**
	 * Lädt den Inhalt des InputStreams local nach remote.
	 */
	public static void storeFile (String remote, InputStream local) throws IOException
	{
		connect();
		System.out.println("storeFile: " + client.printWorkingDirectory() + " / " + remote);
		if (!client.storeFile(remote, local))
			throw new IOException("Failed to store file to " + remote + ": " + client.getReplyString());
		local.close();
	}
	
	/**
	 * Lädt den Inhalt des InputStreams local als Binary-tar-bz2-Archiv der KI
	 * hoch.
	 */
	public static void storeAi (String user, String ai, int version, InputStream local) throws IOException
	{
		storeFile(aiPath(user, ai) + "/v" + version + ".tar.bz2", local);
	}
}

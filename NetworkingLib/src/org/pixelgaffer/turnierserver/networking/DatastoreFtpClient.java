package org.pixelgaffer.turnierserver.networking;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
	private static void connect () throws IOException, FTPIllegalReplyException, FTPException
	{
		if (client == null)
			client = new FTPClient();
		if (!client.isConnected())
		{
			// zum Server verbinden
			client.connect(System.getProperty("turnierserver.datastore.host"),
					Integer.parseInt(System.getProperty("turnierserver.datastore.port")));
			// beim Server anmelden
			System.out.println("logging in …");
			String username = System.getProperty("turnierserver.datastore.username");
			String password = System.getProperty("turnierserver.datastore.password");
			client.login(username, password);
			System.out.println("logged in");
			// passiven Modus benutzen (Firewall …)
			client.setPassive(true);
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
	public static void retrieveFile (String remote, File local)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
	{
		connect();
		client.download(remote, local);
	}
	
	/**
	 * Speichert alles im Remote-Directory im local-Verzeichnis.
	 */
	public static void retrieveDir (String remote, File local)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			FTPListParseException
	{
		connect();
		System.out.println("retrieveDir: " + remote);
		String cwd = client.currentDirectory();
		client.changeDirectory(remote);
		local.mkdirs();
		for (FTPFile f : client.list())
		{
			if (f.getType() == FTPFile.TYPE_FILE)
				retrieveFile(f.getName(), new File(local, f.getName()));
			else if (f.getType() == FTPFile.TYPE_DIRECTORY)
				retrieveDir(f.getName(), new File(local, f.getName()));
		}
		client.changeDirectory(cwd);
	}
	
	/**
	 * Speichert das tar-bzip2-Archiv mit den compilierten Daten der KI im
	 * OutputStream local.
	 */
	public static void retrieveAi (String user, String name, int version, File local)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
	{
		retrieveFile(aiPath(user, name) + "/v" + version + ".tar.bz2", local);
	}
	
	/**
	 * Speichert alle Dateien im Source-Folder der KI in einem temporären
	 * Verzeichnis und gibt dieses zurück.
	 */
	public static File retrieveAiSource (String user, String name, int version)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			FTPListParseException
	{
		File tmp = Files.createTempDirectory("ai").toFile();
		retrieveDir(aiPath(user, name, version), tmp);
		return tmp;
	}
	
	/**
	 * Speichert das jar-Archiv der Gamelogic im OutputStream local.
	 */
	public static void retrieveGameLogic (String game, File local)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
	{
		retrieveFile("Games/" + game + "/Logic.jar", local);
	}
	
	/**
	 * Speichert die AiLibrary der angegebenen Sprache im Verzeichnis local.
	 */
	public static void retrieveAiLibrary (String game, String language, File local)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			FTPListParseException
	{
		retrieveDir("Games/" + game + "/" + language, local);
	}
	
	/**
	 * Speichert die jar-Archive der Bibliothek im Verzeichnis local.
	 */
	public static void retrieveLibrary (String lib, String language, File local)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			FTPListParseException
	{
		retrieveDir("Libraries/" + language + "/" + lib, local);
	}
	
	/**
	 * Lädt den Inhalt des InputStreams local nach remote.
	 */
	public static void storeFile (String remote, InputStream local)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
	{
		connect();
		System.out.println("storeFile: " + client.currentDirectory() + " / " + remote);
		client.upload(remote, local, 0, 0, null);
		local.close();
	}
	
	/**
	 * Lädt den Inhalt des InputStreams local als Binary-tar-bz2-Archiv der KI
	 * hoch.
	 */
	public static void storeAi (String user, String ai, int version, InputStream local)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
	{
		storeFile(aiPath(user, ai) + "/v" + version + ".tar.bz2", local);
	}
}

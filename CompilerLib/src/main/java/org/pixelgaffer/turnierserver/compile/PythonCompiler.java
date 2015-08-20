package org.pixelgaffer.turnierserver.compile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

public class PythonCompiler extends Compiler
{
	public PythonCompiler (int ai, int version, int game)
	{
		super(ai, version, game);
	}
	
	public String getLanguage ()
	{
		return "Python";
	}
	
	@Override
	public boolean compile (File srcdir, File bindir, Properties p, PrintWriter output, LibraryDownloader libraryDownloader)
			throws IOException
	{
		// den wrapper laden
		if (libraryDownloader == null)
		{
			try
			{
				output.print("> Lade game_wrapper.py herunter ... ");
				DatastoreFtpClient.retrieveAiLibrary(getGame(), "Python", bindir);
				output.println("fertig");
				output.print("> Lade wrapper.py herunter ... ");
				DatastoreFtpClient.retrieveLibrary("wrapper", "Python", bindir);
				output.println("fertig");
			}
			catch (IOException | FTPIllegalReplyException | FTPException | FTPDataTransferException
					| FTPAbortedException | FTPListParseException ioe)
			{
				output.println(ioe.getMessage());
				return false;
			}
		}
		else
		{
			output.println("> Keine Verbindung zum FTP-Server");
			libraryDownloader.getAiLibFile("Python", "game_wrapper.py");
			libraryDownloader.getFile("Python", "wrapper", "wrapper.py");
		}
		
		output.print("> Kopiere Quelltext ... ");
		FileUtils.copyDirectory(srcdir, bindir);
		output.println("fertig");
		
		setCommand("python3");
		setArguments(new String[] { "wrapper.py", p.getProperty("filename") });
		
		return true;
	}
	
}

package org.pixelgaffer.turnierserver.compile;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

public class PythonCompiler extends Compiler
{	
	public PythonCompiler (int ai, int version, int game)
	{
		super(ai, version, game);
	}
	
	@Override
	public boolean compile (File srcdir, File bindir, Properties p, PrintWriter output, LibraryDownloader libraryDownloader) throws IOException
	{
		// den wrapper laden
		if (libraryDownloader == null) {
			try {
				output.print("> Lade game_wrapper.py herunter ... ");
				DatastoreFtpClient.retrieveAiLibrary(getGame(), "Python", bindir);
				output.println("fertig");
				output.print("> Lade wrapper.py herunter ... ");
				DatastoreFtpClient.retrieveLibrary("wrapper", "Python", bindir);
				output.println("fertig");
			} catch (IOException | FTPIllegalReplyException | FTPException | FTPDataTransferException
					| FTPAbortedException | FTPListParseException ioe) {
				output.println(ioe.getMessage());
				return false;
			}
		} else {
			output.println("> Keine Verbindung zum FTP-Server");
			libraryDownloader.getAiLibFile("Python", "game_wrapper.py");
			libraryDownloader.getFile("Python", "wrapper", "wrapper.py");
		}
		

		output.print("> Kopiere Quelltext ... ");
		FileUtils.copyDirectory(srcdir, bindir);
		output.println("fertig");
		
		// das script zum starten erzeugen
		output.print("> Erstelle ein Skript zum Starten der KI ... ");
		File scriptFile = new File(bindir, "start.sh");
		scriptFile.createNewFile();
		scriptFile.setExecutable(true);
		if (!scriptFile.canExecute())
		{
			output.println("Konnte das Skript nicht als ausführbar markieren!");
			return false;
		}
		PrintWriter script = new PrintWriter(new FileOutputStream(scriptFile));
		script.println("#!/bin/sh");
		// ## wenn libs pypy: pypy, p2k: python2
		script.println("python3 wrapper.py \"" + p.getProperty("filename") + "\" ${@}");
		script.close();
		setCommand("python3 wrapper.py \"" + p.getProperty("filename"));
		output.println("fertig");
		
		return true;
	}
	
}

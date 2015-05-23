package org.pixelgaffer.turnierserver.compile;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;



public class PythonCompiler extends Compiler
{

	public PythonCompiler (int ai, int version, int game)
	{
		super(ai, version, game);
	}

	@Override
	public boolean compile (File srcdir, File bindir, Properties p, PrintWriter output) throws IOException,
			InterruptedException
	{
		// den wrapper laden
		File game_wrapper = new File(bindir.getPath() + "/game_wrapper.py");
		File wrapper = new File(bindir.getPath() + "/wrapper.py");
		try
		{
			output.println("> downloading game_wrapper.py");
			DatastoreFtpClient.retrieveFile("Games/" + getGame() + "/Python/game_wrapper.py", game_wrapper);
			output.println("> downloading wrapper.py");
			DatastoreFtpClient.retrieveFile("Libraries/Python/wrapper.py", wrapper);
			output.println("done");
		}
		catch (IOException | FTPIllegalReplyException | FTPException | FTPDataTransferException
				| FTPAbortedException ioe)
		{
			output.println(ioe.getMessage());
			return false;
		}


		// das script zum starten erzeugen
		output.println("> creating startup script ... ");
		File scriptFile = new File(bindir, "start.sh");
		scriptFile.createNewFile();
		scriptFile.setExecutable(true);
		if (!scriptFile.canExecute())
		{
			output.println("failed to mark file as executable");
			return false;
		}
		PrintWriter script = new PrintWriter(new FileOutputStream(scriptFile));
		script.println("#!/bin/sh");
		// ## wenn libs pypy: pypy, p2k: python2
		script.println("python3 wrapper.py \""+p.getProperty("filename")+"\" ${@}");
		script.close();
		output.println("done");

		return true;
	}

}

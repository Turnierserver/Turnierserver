package org.pixelgaffer.turnierserver.compile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.compile.LibraryDownloader.LibraryDownloaderMode;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

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
	public boolean compile (File srcdir, File bindir, Properties p, PrintWriter output,
							LibraryDownloader libraryDownloader)
			throws IOException
	{
		// den wrapper laden
		try
		{
			output.print("> Lade game_wrapper.py herunter ... ");
			if (libraryDownloader == null || libraryDownloader.getMode() == LibraryDownloaderMode.LIBS_ONLY)
				DatastoreFtpClient.retrieveAiLibrary(getGame(), "Python", bindir);
			else
				libraryDownloader.getAiLibFile("Python", "game_wrapper.py");
			output.println("fertig");
			
			output.print("> Lade wrapper.py herunter ... ");
			if (libraryDownloader == null)
				DatastoreFtpClient.retrieveLibrary("wrapper", "Python", bindir);
			else
				libraryDownloader.getFile("Python", "wrapper", "wrapper.py");
			output.println("fertig");
			output.println("> Füge die Bibliothek für den wrapper hinzu ... ");
			getLibs().add(new RequiredLibrary("wrapper", "."));
			output.println("fertig");
		}
		catch (Exception e)
		{
			output.println(e);
			return false;
		}
		
		output.print("> Kopiere Quelltext ... ");
		FileUtils.copyDirectory(srcdir, bindir);
		output.println("fertig");
		
		setCommand("python3");
		setArguments(new String[] { "wrapper.py", p.getProperty("filename") });
		
		return true;
	}
	
}

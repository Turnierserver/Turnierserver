/*
 * PythonCompiler.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
				DatastoreFtpClient.retrieveLibrary("wrapper/1", "Python", bindir);
			else
				libraryDownloader.getFile("Python", "wrapper/1", "wrapper.py");
			output.println("fertig");
			output.print("> Füge die Bibliothek für den wrapper hinzu ... ");
			getLibs().add(new RequiredLibrary("wrapper/1", "."));
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

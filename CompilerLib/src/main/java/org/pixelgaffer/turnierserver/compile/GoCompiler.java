/*
 * GoCompiler.java
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.compile.LibraryDownloader.LibraryDownloaderMode;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

public class GoCompiler extends Compiler
{
	public GoCompiler (int ai, int version, int game)
	{
		super(ai, version, game);
	}
	
	public String getLanguage ()
	{
		return "Go";
	}
	
	@Override
	public boolean compile (File srcdir, File bindir, Properties p, PrintWriter output,
							LibraryDownloader libraryDownloader)
			throws IOException
	{

		output.print("> Kopiere Wrapper ... ");
		Path wrapperdir = Files.createTempDirectory("go-wrapper-");
		File wrappersrc = new File(wrapperdir.toFile(), "src/main");
		Files.createDirectories(wrappersrc.toPath());
		try {
			DatastoreFtpClient.retrieveAiLibrary(getGame(), getLanguage(), wrappersrc);
		} catch (FTPIllegalReplyException | FTPException | FTPDataTransferException | FTPAbortedException
				| FTPListParseException e) {
			output.println("FTP-Fehler " + e.toString());
			return false;
		}
		output.println("fertig");

		try
		{
			output.println("> Baue Quelldatei(en)  ... ");
			HashMap<String, String> env = new HashMap<String, String>();
			List<String> gopath = new ArrayList<>();
			if (System.getenv("GOPATH") != null)
				gopath = new ArrayList<>(Arrays.asList(System.getenv("GOPATH").split(":")));

			gopath.add(srcdir.getAbsolutePath());
			
			if (wrapperdir != null)
				gopath.add(wrapperdir.toFile().getAbsolutePath());

			env.put("GOPATH", String.join(":", gopath));
			
			int returncode = execute(srcdir, output, env, "go", "build", "-o", "executable", "main");
			if (returncode != 0)
			{
				output.println("Process finished with exit code " + returncode + ", aborting");
				return false;
			}
			output.println("fertig");
		}
		catch (Exception e)
		{
			output.println(e);
			return false;
		}
		
		output.print("> Räume auf ... ");
		FileUtils.deleteDirectory(wrapperdir.toFile());
		output.println("fertig");
		
		output.print("> Kopiere Executable ... ");
		FileUtils.copyFile(new File(srcdir, "executable"), new File(bindir, "executable"));
		output.println("fertig");
		
		setCommand("./executable");
		setArguments(new String[] { });
		
		return true;
	}
	
}

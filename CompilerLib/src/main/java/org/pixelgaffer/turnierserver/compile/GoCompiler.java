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
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

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
		// den wrapper laden
		try
		{
			output.println("> Baue Quelldatei(en)  ... ");
			HashMap<String, String> env = new HashMap<String, String>();
			env.put("GOPATH", System.getenv("GOPATH") + ":" + srcdir.getAbsolutePath());
			execute(srcdir, output, env, "bash", "-c", "echo $GOPATH");
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
		
		output.print("> Kopiere Ordner ... ");
		FileUtils.copyDirectory(srcdir, bindir);
		output.println("fertig");
		
		setCommand("executable");
		setArguments(new String[] { });
		
		return true;
	}
	
}

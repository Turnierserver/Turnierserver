/*
 * CCompiler.java
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.compile.LibraryDownloader.LibraryDownloaderMode;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

public class CCompiler extends Compiler
{
	public CCompiler (int ai, int version, int game)
	{
		super(ai, version, game);
	}
	
	@Override
	public String getLanguage ()
	{
		return "C";
	}
	
	@Override
	public boolean compile (File srcdir, File bindir, Properties p, PrintWriter output, LibraryDownloader libs)
			throws IOException, InterruptedException
	{
		Set<File> librarys = new HashSet<>(), includepath = new HashSet<>();
		includepath.add(srcdir);
		String librarypath;
		
		// die AiLibrary laden
		output.print("> Lade Ai Bibliothek herunter ... ");
		output.flush();
		File libdir = new File(bindir, "AiLibrary");
		librarypath = libdir.getName();
		libdir.mkdir();
		try
		{
			if (libs == null || libs.getMode() == LibraryDownloaderMode.LIBS_ONLY)
				DatastoreFtpClient.retrieveAiLibrary(getGame(), "C", libdir);
			else
			{
				for (File f : libs.getAiLibs("C"))
					FileUtils.copyFile(f, new File(libdir, f.getName()));
			}
			includepath.add(libdir);
			for (File so : libdir.listFiles( (dir, name) -> name.endsWith(".so")))
				librarys.add(so);
			output.println("done");
		}
		catch (Exception e)
		{
			libdir.delete();
			output.println(e);
			return false;
		}
		
		// kompilieren
		getEnvironment().put("LD_LIBRARY_PATH", librarypath);
		Set<File> objects = new HashSet<>();
		if (!compileRecursive(srcdir, p, librarys, includepath, srcdir, bindir, objects, output))
			return false;
			
		// linken
		List<String> command = new LinkedList<>();
		command.add("g++");
		File exec = new File(bindir, "ai");
		command.add("-o");
		command.add(exec.getName());
		if (Boolean.parseBoolean(p.getProperty("debug", "false")))
		{
			command.add("-g");
			command.add("-rdynamic");
		}
		else
			command.add("-Wl,-O2");
		for (File obj : objects)
			command.add(obj.getName());
		command.add("-lm"); // math lib
		for (File lib : librarys)
		{
			command.add("-L" + lib.getParentFile().getAbsolutePath());
			// cut of the trailing lib and the leading .so
			command.add("-l" + lib.getName().substring(3, lib.getName().length() - 3));
		}
		int returncode = execute(bindir, output, command);
		if (returncode != 0)
		{
			output.println("Process finished with exit code " + returncode + ", aborting");
			return false;
		}
		for (File obj : objects) // ich brauche die .o net mehr
			obj.delete();
		
		setCommand("./" + exec.getName());
		setArguments(new String[] {});
		return true;
	}
	
	protected boolean compileRecursive (File currentDir, Properties p, Set<File> librarys, Set<File> includepath,
			File srcdir, File bindir, Set<File> objects, PrintWriter output)
					throws IOException, InterruptedException
	{
		for (String filename : currentDir.list())
		{
			File file = new File(currentDir, filename);
			if (file.isDirectory())
			{
				if (!compileRecursive(file, p, librarys, includepath, srcdir, bindir, objects, output))
					return false;
				continue;
			}
			
			// .c, .cpp & .cxx -Dateien kompilieren
			if (filename.endsWith(".c") || filename.endsWith(".cpp") || filename.endsWith(".cxx"))
			{
				boolean c = filename.endsWith(".c");
				List<String> command = new LinkedList<>();
				command.add(c ? "gcc" : "g++");
				if (filename.endsWith(".c"))
					command.add("-std=" + (p.getProperty("standart.c.gnu", "false").equalsIgnoreCase("true") ? "gnu" : "c")
							+ p.getProperty("standart.c.version", "90"));
				else
					command.add("-std=" + (p.getProperty("standart.cpp.gnu", "false").equalsIgnoreCase("true") ? "gnu" : "c")
							+ "++" + p.getProperty("standart.cpp.version", "98"));
				File out = new File(bindir, filename.substring(0, filename.lastIndexOf('.')) + ".o");
				command.add("-o");
				command.add(out.getName());
				if (Boolean.parseBoolean(p.getProperty("debug", "false")))
					command.add("-g");
				else
					command.add("-O2");
				command.add("-c");
				command.add(file.getAbsolutePath());
				for (File incpath : includepath)
					command.add("-I" + incpath.getAbsolutePath());
				int returncode = execute(bindir, output, getEnvironment(), command);
				if (returncode != 0)
				{
					output.println("Process finished with exit code " + returncode + ", aborting");
					return false;
				}
				objects.add(out);
			}
			
			else if (!filename.endsWith(".h") && !filename.endsWith(".hxx"))
			{
				// die Datei ins bindir kopieren
				String relative = relativePath(file, srcdir);
				copy(file, new File(bindir, relative));
			}
		}
		
		return true;
	}
}

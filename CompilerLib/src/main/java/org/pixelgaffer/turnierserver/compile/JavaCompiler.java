/*
 * JavaCompiler.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.compile.LibraryDownloader.LibraryDownloaderMode;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

public class JavaCompiler extends Compiler
{
	public JavaCompiler (int ai, int version, int game)
	{
		super(ai, version, game);
	}
	
	@Override
	public String getLanguage ()
	{
		return "Java";
	}
	
	@Override
	public boolean compile (File srcdir, File bindir, Properties p, PrintWriter output, LibraryDownloader libs)
			throws IOException, InterruptedException
	{
		String classpath = ".";
		
		// die AiLibrary laden
		output.print("> Lade Ai Bibliothek herunter ... ");
		output.flush();
		File libdir = new File(bindir, "AiLibrary");
		libdir.mkdir();
		try
		{
			if (libs == null || libs.getMode() == LibraryDownloaderMode.LIBS_ONLY)
				DatastoreFtpClient.retrieveAiLibrary(getGame(), "Java", libdir);
			else
			{
				for (File f : libs.getAiLibs("Java"))
					FileUtils.copyFile(f, new File(libdir, f.getName()));
			}
			for (String jar : libdir.list( (dir, name) -> name.endsWith(".jar")))
				classpath += File.pathSeparator + "AiLibrary" + File.separator + jar;
			output.println("done");
		}
		catch (Exception e)
		{
			libdir.delete();
			output.println(e);
			return false;
		}
		
		// die benötigten Libraries lesen
		BufferedReader libraries = new BufferedReader(new FileReader(new File(srcdir, "libraries.txt")));
		String line;
		while ((line = libraries.readLine()) != null)
		{
			line = line.trim();
			if ((line.length() == 0) || line.startsWith("#"))
				continue;
			String libdirname = line.replace('/', '_');
				
			output.print("> Lade Bibliothek " + line + " herunter ... ");
			output.flush();
			libdir = new File(bindir, libdirname);
			libdir.mkdir();
			getLibs().add(new RequiredLibrary(line, libdirname));
			try
			{
				if (libs == null)
					DatastoreFtpClient.retrieveLibrary(line, "Java", libdir);
				else
				{
					for (File f : libs.getLib("Java", line))
						FileUtils.copyFile(f, new File(libdir, f.getName()));
				}
				for (String jar : libdir.list( (dir, name) -> name.endsWith(".jar")))
					classpath += ":" + libdirname + File.separator + jar;
				output.println("done");
			}
			catch (Exception e)
			{
				libdir.delete();
				output.println(e.getMessage());
			}
		}
		libraries.close();
		
		// die Klassen kompilieren
		if (!compileRecursive(srcdir, classpath, srcdir, bindir, output))
			return false;
			
		// die argumente für den java befehl speichern
		String mainclass = p.getProperty("mainclass");
		if (!Pattern.matches("[a-zA-Z0-9_\\.]+", mainclass))
		{
			output.println(mainclass + " ist keine valide Klasse");
			return false;
		}
		if (mainclass == null)
		{
			output.println("failed");
			output.println("FEHLER: Die Hauptklasse wurde nicht in der settings.prop-Datei angegeben!");
			output.println("        Bitte die Zeile");
			output.println("            mainclass=com.example.Ai");
			output.println("        der settings.prop hinzufügen!");
			return false;
		}
		setCommand("java");
		String args[] = { "-classpath", classpath, "-Xmx500M", mainclass };
		setArguments(args);
		
		return true;
	}
	
	protected boolean compileRecursive (File currentDir, String classpath, File srcdir, File bindir, PrintWriter output)
			throws IOException, InterruptedException
	{
		for (String filename : currentDir.list())
		{
			File file = new File(currentDir, filename);
			if (file.isDirectory())
			{
				if (!compileRecursive(file, classpath, srcdir, bindir, output))
					return false;
				continue;
			}
			
			// die Datei ins bindir kopieren
			String relative = relativePath(file, srcdir);
			copy(file, new File(bindir, relative));
			
			// .java-Dateien kompilieren
			if (filename.endsWith(".java"))
			{
				int returncode = execute(bindir, output, "javac", "-classpath", classpath, "-implicit:none", relative);
				if (returncode != 0)
				{
					output.println("Process finished with exit code " + returncode + ", aborting");
					return false;
				}
				// ich brauch die source nicht mehr
				new File(bindir, relative).delete();
			}
		}
		
		return true;
	}
}

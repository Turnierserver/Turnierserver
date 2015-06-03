package org.pixelgaffer.turnierserver.compile;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

public class JavaCompiler extends Compiler
{
	public JavaCompiler (int ai, int version, int game)
	{
		super(ai, version, game);
	}
	
	@Override
	public boolean compile (File srcdir, File bindir, Properties p, PrintWriter output, LibraryDownloader libs)
			throws IOException, InterruptedException
	{
		String classpath = ".";
		
		// die AiLibrary laden
		output.print("> downloading ai library ... ");
		File libdir = new File(bindir, "AiLibrary");
		libdir.mkdir();
		try
		{
			if (libs == null)
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
		catch (IOException | FTPIllegalReplyException | FTPException | FTPDataTransferException
				| FTPAbortedException | FTPListParseException ioe)
		{
			libdir.delete();
			output.println(ioe);
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
			
			output.print("> downloading library " + line + " ... ");
			libdir = new File(bindir, line);
			libdir.mkdir();
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
					classpath += ":" + line + File.separator + jar;
				output.println("done");
			}
			catch (IOException | FTPIllegalReplyException | FTPException | FTPDataTransferException
					| FTPAbortedException | FTPListParseException ioe)
			{
				libdir.delete();
				output.println(ioe.getMessage());
			}
		}
		libraries.close();
		
		// die Klassen kompilieren
		if (!compileRecursive(srcdir, classpath, srcdir, bindir, output))
			return false;
		
		// das script zum starten erzeugen
		output.print("> creating startup script ... ");
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
		String mainclass = p.getProperty("mainclass");
		if (mainclass == null)
		{
			output.println("failed");
			output.println("FEHLER: Die Hauptklasse wurde nicht in der settings.prop-Datei angegeben!");
			output.println("        Bitte die Zeile");
			output.println("            mainclass=com.example.Ai");
			output.println("        der settings.prop hinzufügen!");
			script.println("echo missing mainclass property");
			script.close();
			return false;
		}
		String command = "java -classpath '" + classpath + "' -Xmx500M '"
				+ mainclass.replace("'", "\\'") + "' ${@}";
		setCommand(command);
		script.println("echo \"" + command + "\"");
		script.println(command);
		script.close();
		output.println("done");
		
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

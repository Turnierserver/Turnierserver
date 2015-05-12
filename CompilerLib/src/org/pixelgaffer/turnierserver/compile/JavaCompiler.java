package org.pixelgaffer.turnierserver.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

public class JavaCompiler extends Compiler
{
	public JavaCompiler (String user, String ai, int version)
	{
		super(user, ai, version);
	}
	
	@Override
	public boolean compile (File srcdir, File bindir, PrintWriter output) throws IOException, InterruptedException
	{
		String classpath = ".";
		
		// die benötigten Libraries lesen
		BufferedReader libraries = new BufferedReader(new FileReader(new File(srcdir, "libraries.txt")));
		String line;
		while ((line = libraries.readLine()) != null)
		{
			line = line.trim();
			if ((line.length() == 0) || line.startsWith("#"))
				continue;
			
			output.print("downloading library " + line + " ... ");
			File libdir = new File(bindir, line);
			libdir.mkdir();
			try
			{
				DatastoreFtpClient.retrieveLibrary(line, "Java", libdir);
				for (String jar : libdir.list( (dir, name) -> name.endsWith(".jar")))
					classpath += ":" + line + "/" + jar;
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
		compileRecursive(srcdir, classpath, srcdir, bindir, output);
		
		// aktuell ist die kompilierung noch unmöglich
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
				execute(bindir, output, "javac", "-classpath", classpath, "-implicit:none", relativePath(currentDir,
						srcdir)
						+ "/" + filename);
			}
		}
		
		return true;
	}
}

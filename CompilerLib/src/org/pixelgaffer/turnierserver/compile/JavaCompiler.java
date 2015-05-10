package org.pixelgaffer.turnierserver.compile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

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
			
			File libdir = new File(bindir, line);
			libdir.mkdir();
			DatastoreFtpClient.retrieveLibrary(line, "Java", libdir);
			classpath += ":" + line + "/*";
		}
		
		// aktuell ist die kompilierung noch unmöglich
		return false;
	}
}

package org.pixelgaffer.turnierserver.compile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

/**
 * Diese Klasse ist eine abstrakte Implementation eines Compilers, der die
 * Verbindung zum FTP-Server größtenteils übernimmt.
 */
@AllArgsConstructor
public abstract class Compiler
{
	@Getter
	private String user, ai;
	@Getter
	private int version;
	
	public CompileResult compileAndUpload () throws IOException, InterruptedException
	{
		// source runterladen
		File srcdir = DatastoreFtpClient.retrieveAiSource(getUser(), getAi(), getVersion());
		
		// zeugs anlegen
		File bindir = Files.createTempDirectory("aibin").toFile();
		File output = Files.createTempFile("compiler", ".txt").toFile();
		PrintWriter pw = new PrintWriter(new FileOutputStream(output), true);
		
		// compilieren
		boolean success = compile(srcdir, bindir, pw);
		
		// aufräumen
		// srcdir.delete();
		
		if (success)
		{
			// packen
			File archive = Files.createTempFile("aibin", ".tar.bz2").toFile();
			execute(bindir, pw, "tar", "cfj", archive.getAbsolutePath(), "*");
			
			// hochladen
			DatastoreFtpClient.storeAi(getUser(), getAi(), new FileInputStream(archive));
			
			// aufräumen
			// archive.delete();
		}
		
		// aufräumen
		// bindir.delete();
		
		pw.close();
		return new CompileResult(success, output);
	}
	
	public abstract boolean compile (File srcdir, File bindir, PrintWriter output)
			throws IOException, InterruptedException;
	
	protected int execute (File wd, PrintWriter output, String ... command) throws IOException, InterruptedException
	{
		output.print("$");
		for (String cmd : command)
		{
			if (cmd.contains(" "))
				cmd = "\"" + cmd + "\"";
			output.print(" ");
			output.print(cmd);
		}
		output.println();
		
		ProcessBuilder pb = new ProcessBuilder(command);
		if (wd != null)
			pb.directory(wd);
		Process p = pb.start();
		return p.waitFor();
	}
	
	public static void main (String args[]) throws IOException, InterruptedException
	{
		Properties p = new Properties(System.getProperties());
		p.load(new FileInputStream(args.length > 0 ? args[0] : "/etc/turnierserver/turnierserver.prop"));
		System.setProperties(p);
		
		Compiler comp = new JavaCompiler("Nico", "MinesweeperAi", 1);
		comp.compileAndUpload();
	}
}

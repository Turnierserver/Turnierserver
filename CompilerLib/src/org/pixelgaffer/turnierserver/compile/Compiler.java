package org.pixelgaffer.turnierserver.compile;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.pixelgaffer.turnierserver.PropertiesLoader;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;

/**
 * Diese Klasse ist eine abstrakte Implementation eines Compilers, der die
 * Verbindung zum FTP-Server größtenteils übernimmt.
 */
@AllArgsConstructor
public abstract class Compiler
{
	public static Compiler getCompiler (String user, String ai, int version, String language)
			throws ReflectiveOperationException
	{
		Class<?> clazz = Class.forName("org.pixelgaffer.turnierserver.compile." + language + "Compiler");
		Compiler c = (Compiler)clazz
				.getConstructor(String.class, String.class, Integer.TYPE)
				.newInstance(user, ai, version);
		return c;
	}
	
	@Getter
	private String user, ai;
	@Getter
	private int version;
	
	public CompileResult compileAndUpload ()
			throws IOException, InterruptedException, FTPIllegalReplyException, FTPException, FTPDataTransferException,
			FTPAbortedException, FTPListParseException
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
			String files[] = bindir.list( (dir, name) -> !name.startsWith("libraries"));
			String cmd[] = new String[files.length + 3];
			cmd[0] = "tar";
			cmd[1] = "cfj";
			cmd[2] = archive.getAbsolutePath();
			System.arraycopy(files, 0, cmd, 3, files.length);
			System.out.println(execute(bindir, pw, cmd));
			
			// hochladen
			DatastoreFtpClient.storeAi(getUser(), getAi(), getVersion(), new FileInputStream(archive));
			
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
	
	protected String relativePath (File absolute, File base)
	{
		Path absolutePath = absolute.toPath();
		Path basePath = base.toPath();
		Path relative = basePath.relativize(absolutePath);
		return relative.toString();
	}
	
	protected void copy (File in, File out) throws IOException
	{
		System.out.println("copy: " + in + " → " + out);
		out.mkdirs();
		out.delete();
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		byte buf[] = new byte[8192];
		int read;
		while ((read = fis.read(buf)) > 0)
			fos.write(buf, 0, read);
		fis.close();
		fos.close();
	}
	
	protected int execute (File wd, PrintWriter output, String ... command) throws IOException, InterruptedException
	{
//		output.print(wd.getAbsolutePath());
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
		File log = Files.createTempFile("compiler", ".txt").toFile();
		pb.redirectErrorStream(true);
		pb.redirectOutput(log);
		if (wd != null)
			pb.directory(wd);
		Process p = pb.start();
		int returncode = p.waitFor();
		Reader in = new FileReader(log);
		char buf[] = new char[8192]; int read;
		while ((read = in.read(buf)) > 0)
			output.write(buf, 0, read);
		in.close();
		output.flush();
		return returncode;
	}
	
	public static void main (String args[])
			throws IOException, InterruptedException, FTPIllegalReplyException, FTPException, FTPDataTransferException,
			FTPAbortedException, FTPListParseException
	{
		PropertiesLoader.loadProperties(args.length > 0 ? args[0] : "/etc/turnierserver/turnierserver.prop");
		
		Compiler comp = new JavaCompiler("Nico", "MinesweeperAi", 1);
		CompileResult r = comp.compileAndUpload();
		System.out.println("---------------------------------------------------------------------------------------");
		FileInputStream fis = new FileInputStream(r.getOutput());
		byte buf[] = new byte[8192];
		int read;
		while ((read = fis.read(buf)) != -1)
			System.out.write(buf, 0, read);
		fis.close();
		System.out.println(r.isSuccessfull());
	}
}

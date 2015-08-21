package org.pixelgaffer.turnierserver.compile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import org.pixelgaffer.turnierserver.networking.bwprotocol.WorkerCommandAnswer;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Diese Klasse ist eine abstrakte Implementation eines Compilers, der die
 * Verbindung zum FTP-Server größtenteils übernimmt.
 */
public abstract class Compiler
{
	public Compiler (int ai, int version, int game)
	{
		super();
		this.ai = ai;
		this.version = version;
		this.game = game;
	}
	
	public static Compiler getCompiler (String language) throws ReflectiveOperationException
	{
		return getCompiler(-1, -1, -1, language);
	}
	
	public static Compiler getCompiler (int ai, int version, int game, String language)
			throws ReflectiveOperationException
	{
		Class<?> clazz = Class.forName("org.pixelgaffer.turnierserver.compile." + language + "Compiler");
		Compiler c = (Compiler)clazz
				.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE)
				.newInstance(ai, version, game);
		return c;
	}
	
	/**
	 * Diese Klasse wird verwendet, um Ausgaben beim Kompilieren einer KI an
	 * Frontend und FTP weiterzuleiten.
	 */
	@RequiredArgsConstructor
	private class CompilerDebugWriter extends Writer
	{
		@NonNull
		private Writer	ftpFile;
		@NonNull
		private Backend	backend;
		
		private String buf = "";
		
		@Override
		public void close () throws IOException
		{
			flush();
			ftpFile.close();
		}
		
		@Override
		public void flush () throws IOException
		{
			ftpFile.flush();
			if (getUuid() != null)
			{
				backend.sendAnswer(new WorkerCommandAnswer(WorkerCommand.COMPILE, WorkerCommandAnswer.MESSAGE,
						getUuid(), buf));
			}
			buf = "";
		}
		
		@Override
		public void write (char[] buf, int off, int len) throws IOException
		{
			write(new String(buf, off, len));
		}
		
		@Override
		public void write (@NonNull String s) throws IOException
		{
			ftpFile.write(s);
			buf += s;
		}
	}
	
	@Getter
	private int	ai;
	@Getter
	private int	version;
	@Getter
	private int	game;
	
	@Getter
	@Setter
	private UUID uuid;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String command;
	
	@Getter
	@Setter(AccessLevel.PROTECTED)
	private String arguments[];
	
	public abstract String getLanguage();
	
	@AllArgsConstructor
	@EqualsAndHashCode
	@ToString
	protected class RequiredLibrary
	{
		public String	name;
		public String	path;
	}
	
	@Getter(AccessLevel.PROTECTED)
	private final Set<RequiredLibrary> libs = new HashSet<>();
	
	public CompileResult compileAndUpload (@NonNull Backend backend, LibraryDownloader libs)
			throws IOException, InterruptedException, FTPIllegalReplyException, FTPException, FTPDataTransferException,
			FTPAbortedException, FTPListParseException
	{
		// source runterladen
		if (backend != null)
			backend.sendAnswer(new WorkerCommandAnswer(WorkerCommand.COMPILE, WorkerCommandAnswer.MESSAGE,
					getUuid(), "> Lade Quelltext herunter ...\n"));
		File srcdir = DatastoreFtpClient.retrieveAiSource(getAi(), getVersion());
		
		// zeugs anlegen
		File bindir = Files.createTempDirectory("aibin").toFile();
		File output = Files.createTempFile("compiler", ".txt").toFile();
		FileWriter ftpFile = new FileWriter(output);
		Writer w = new CompilerDebugWriter(ftpFile, backend);
		PrintWriter pw = new PrintWriter(w, true);
		
		// properties lesen
		Properties p = new Properties();
		try
		{
			p.load(new FileInputStream(new File(srcdir, "settings.prop")));
		}
		catch (IOException ioe)
		{
			pw.println("> Fehler beim Lesen der Datei settings.prop: " + ioe);
			pw.close();
			srcdir.delete();
			bindir.delete();
			return new CompileResult(false, output);
		}
		
		// compilieren
		boolean success = compile(srcdir, bindir, p, pw, libs);
		
		// aufräumen
		srcdir.delete();
		
		if (success)
		{
			// die ini-datei für die sandboxen schreiben
			writeStartIni(bindir);
			
			// packen
			File archive = Files.createTempFile("aibin", ".tar.bz2").toFile();
			List<String> ignored = getLibs().stream().map( (lib) -> lib.path).collect(Collectors.toList());
			String files[] = bindir.list(
					(dir, name) -> !name.equals("libraries.txt") && !name.equals("settings.prop") && !ignored.contains(name));
			String cmd[] = new String[files.length + 3];
			cmd[0] = "tar";
			cmd[1] = "cfj";
			cmd[2] = archive.getAbsolutePath();
			System.arraycopy(files, 0, cmd, 3, files.length);
			System.out.println(execute(bindir, pw, cmd));
			
			// hochladen
			if (backend != null)
				backend.sendAnswer(new WorkerCommandAnswer(WorkerCommand.COMPILE, WorkerCommandAnswer.MESSAGE,
						getUuid(), "> Lade kompilierte KI hoch ...\n"));
			DatastoreFtpClient.storeAi(getAi(), getVersion(), new FileInputStream(archive));
			
			// aufräumen
			archive.delete();
		}
		
		// aufräumen
		bindir.delete();
		
		pw.close();
		return new CompileResult(success, output);
	}
	
	/**
	 * Diese Methode kompiliert den Quelltext einer KI aus srcdir nach bindir.
	 * In der Datei properties stehen die zur KI gehörenden Eigenschaften wie
	 * z.B. die Main-Klasse in Java.
	 */
	public String compile (File srcdir, File bindir, File properties, LibraryDownloader libs)
			throws IOException, InterruptedException, CompileFailureException
	{
		if (!bindir.exists() && !bindir.mkdirs())
			throw new CompileFailureException("Konnte das Verzeichnis " + bindir + " nicht anlegen!");
			
		// den output in einen String ausgeben
		StringWriter sw = new StringWriter();
		PrintWriter output = new PrintWriter(sw);
		
		// die properties laden
		Properties p = new Properties();
		p.load(new FileReader(properties));
		
		// kompilieren
		boolean success = compile(srcdir, bindir, p, output, libs);
		
		// die ini-datei für die sandbox schreiben
		writeStartIni(bindir);
		
		output.flush();
		if (success)
			return sw.toString();
		else
			throw new CompileFailureException(sw.toString());
	}
	
	public abstract boolean compile (File srcdir, File bindir, Properties p, PrintWriter output, LibraryDownloader libs)
			throws IOException, InterruptedException;
	
	private void writeStartIni (File bindir) throws IOException
	{
		PrintWriter start = new PrintWriter(new FileWriter(new File(bindir, "start.ini")));
		start.println("# GENERATED FILE - DO NOT EDIT");
		start.println("Language=" + getLanguage());
		start.println("Command=" + getCommand());
		start.print("Arguments=");
		for (int i = 0; i < getArguments().length; i++)
		{
			if (i > 0)
				start.print(",");
			start.print(getArguments()[i].replace("\\", "\\\\").replace(",", "\\,"));
		}
		start.println();
		start.println("Libraries=" + getLibs().size());
		int count = 0;
		for (RequiredLibrary lib : getLibs())
		{
			start.println("[Lib" + count + "]");
			start.println("Name=" + lib.name);
			start.println("Path=" + lib.path);
			count++;
		}
		start.close();
	}
	
	protected String relativePath (File absolute, File base)
	{
		Path absolutePath = absolute.toPath();
		Path basePath = base.toPath();
		Path relative = basePath.relativize(absolutePath);
		return relative.toString();
	}
	
	protected void copy (File in, File out) throws IOException
	{
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
		char buf[] = new char[8192];
		int read;
		while ((read = in.read(buf)) > 0)
			output.write(buf, 0, read);
		in.close();
		log.delete();
		output.flush();
		return returncode;
	}
}

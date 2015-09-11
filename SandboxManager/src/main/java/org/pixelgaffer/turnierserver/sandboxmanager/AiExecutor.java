package org.pixelgaffer.turnierserver.sandboxmanager;

import static java.lang.Math.min;
import static org.pixelgaffer.turnierserver.FileOwnerChanger.changeOwner;
import static org.pixelgaffer.turnierserver.PropertyUtils.getStringRequired;
import static org.pixelgaffer.turnierserver.sandboxmanager.SandboxMain.commands;
import static org.pixelgaffer.turnierserver.sandboxmanager.SandboxMain.etc;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import lombok.Getter;
import lombok.SneakyThrows;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class AiExecutor implements Runnable
{
	@SuppressWarnings("serial")
	public class AiStartException extends RuntimeException
	{
		public AiStartException ()
		{
			super();
		}
		
		public AiStartException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
		{
			super(message, cause, enableSuppression, writableStackTrace);
		}
		
		public AiStartException (String message, Throwable cause)
		{
			super(message, cause);
		}
		
		public AiStartException (String message)
		{
			super(message);
		}
		
		public AiStartException (Throwable cause)
		{
			super(cause);
		}
	}
	
	@Getter
	private Job job;
	@Getter
	private JobControl jobControl;
	
	@Getter
	private int boxid;
	private String boxdir;
	private File dir, binArchive, binDir, aiProp;
	private Properties start;
	private Process proc;
	
	public AiExecutor (Job job, JobControl ctrl) throws AiStartException
	{
		this.job = job;
		jobControl = ctrl;
		
		// isolate id finden
		boxid = (getJob().getId() + 100) % 100;
		
		// isolate initialisieren
		try
		{
			ProcessBuilder pb = new ProcessBuilder("isolate", "--cg", "--init", "-b", Integer.toString(boxid));
			System.out.println("$ " + pb.command());
			Process p = pb.start();
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			boxdir = in.readLine();
			if (p.waitFor() != 0)
				throw new AiStartException("Error while initialising isolate (exit code: " + p.exitValue() + ")");
			dir = new File(boxdir, "box");
			dir.mkdirs();
		}
		catch (AiStartException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new AiStartException(e);
		}
	}
	
	@SneakyThrows
	@Override
	public void run ()
	{
		download();
		generateProps();
		executeAi();
	}
	
	private static void makeReadOnly (File f, boolean executable) throws IOException
	{
		changeOwner(f, "root");
		f.setExecutable(executable);
		f.setReadable(true);
		f.setWritable(true, true);
	}
	
	private static void extract (File f, File d) throws IOException
	{
		System.out.println("extract(" + f + ", " + d + ")");
		TarArchiveInputStream in = new TarArchiveInputStream(new BZip2CompressorInputStream(new FileInputStream(f)));
		TarArchiveEntry entry;
		while ((entry = in.getNextTarEntry()) != null)
		{
			if (entry.isDirectory())
				new File(d, entry.getName()).mkdir();
			else
			{
				File file = new File(d, entry.getName());
				OutputStream out = new FileOutputStream(file);
				long size = entry.getSize();
				long read = 0;
				while (size > read)
				{
					int toRead = (int)min(size - read, Integer.MAX_VALUE);
					while (toRead > 0)
					{
						byte buf[] = new byte[min(toRead, 8192)];
						int r = in.read(buf);
						if (r <= 0)
						{
							out.close();
							in.close();
							throw new EOFException();
						}
						toRead -= r;
						read += r;
						out.write(buf, 0, r);
					}
				}
				out.close();
			}
		}
		in.close();
	}
	
	protected void download () throws NoSuchAlgorithmException, IOException
	{
		binArchive = new File(dir, "bin.tar.bz2");
		MirrorClient.retrieveAi(getJob().getId(), getJob().getVersion(), binArchive.getAbsolutePath());
		makeReadOnly(binArchive, false);
		binDir = new File(dir, "bin");
		binDir.mkdir();
		extract(binArchive, binDir);
		
		start = new Properties();
		start.load(new FileInputStream(new File(binDir, "start.prop")));
		int libs = Integer.parseInt(start.getProperty("libraries.size"));
		SandboxMain.getLogger().debug("Libraries: " + libs);
		for (int i = 0; i < libs; i++)
		{
			File path = new File(binDir, start.getProperty("libraries." + i + ".path"));
			path.mkdirs();
			MirrorClient.retrieveLib(getJob().getLang(), start.getProperty("libraries." + i + ".name"),
					new File(path, ".tar.bz2").getAbsolutePath());
			makeReadOnly(new File(path, ".tar.bz2"), false);
			extract(new File(path, ".tar.bz2"), path);
		}
	}
	
	protected void generateProps () throws IOException
	{
		aiProp = new File(dir, "ai.prop");
		Properties aiProps = new Properties();
		aiProps.put("turnierserver.worker.host", getStringRequired("worker.host"));
		aiProps.put("turnierserver.worker.server.port", getStringRequired("worker.port"));
		aiProps.put("turnierserver.worker.server.aichar", "A");
		aiProps.put("turnierserver.serializer.compress.worker", getStringRequired("turnierserver.serializer.compress.worker"));
		aiProps.put("turnierserver.ai.uuid", getJob().getUuid().toString());
		aiProps.store(new FileOutputStream(aiProp), "GENERATED FILE - DO NOT EDIT");
		makeReadOnly(aiProp, false);
	}
	
	protected void executeAi () throws IOException
	{
		List<String> cmd = new LinkedList<>();
		cmd.add("isolate");
		String command;
		if (commands.get(getJob().getLang()).isEmpty())
			command = start.getProperty("command");
		else
			command = commands.get(getJob().getLang());
		cmd.add("--cg");
		cmd.add("-p");
		cmd.add("--share-net");
		cmd.add("-q"); cmd.add("0,0");
		cmd.add("--dir=/etc/=" + etc.getAbsolutePath());
		cmd.add("--dir=/usr/lib/jvm/");
		cmd.add("-c"); cmd.add("/box/bin/");
		cmd.add("--env=LANG");
		cmd.add("--run");
		cmd.add("-b"); cmd.add(Integer.toString(boxid));
		cmd.add("--");
		cmd.add(command);
		for (int i = 0; i < Integer.parseInt(start.getProperty("arguments.size")); i++)
		{
			cmd.add(start.getProperty("arguments." + i));
		}
		cmd.add("/box/" + aiProp.getName());
		SandboxMain.getLogger().debug("Der Befehl ist " + cmd);
		
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);
		pb.redirectOutput(Redirect.INHERIT);
		proc = pb.start();
		SandboxMain.getClient().sendMessage(getJob().getUuid(), 'S');
		
		new Thread (() -> {
			int ret;
			try
			{
				ret = proc.waitFor();
				SandboxMain.getLogger().debug("Die KI hat sich mit dem Statuscode " + ret + " beendet");
				SandboxMain.getClient().sendMessage(getJob().getUuid(), 'F');
				new ProcessBuilder("isolate", "--cleanup", "-b", Integer.toString(boxid)).start().waitFor();
				jobControl.jobFinished(getJob().getUuid());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}, "IsolateCleanup").start();
	}
	
	public void terminateAi ()
	{
		SandboxMain.getLogger().info("terminiere " + getJob());
		proc.destroyForcibly();
		SandboxMain.getClient().sendMessage(getJob().getUuid(), 'T');
	}
	
	public void killAi ()
	{
		SandboxMain.getLogger().info("töte " + getJob());
		proc.destroyForcibly();
		SandboxMain.getClient().sendMessage(getJob().getUuid(), 'K');
	}
}

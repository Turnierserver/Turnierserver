package org.pixelgaffer.turnierserver.worker.run;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.util.Properties;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import org.pixelgaffer.turnierserver.PropertyUtils;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;
import org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType;

@ToString(of = { "aiId", "aiVersion", "game", "uuid" })
public class AiRunner
{
	@Getter
	private int aiId, aiVersion;
	
	@Getter
	private int game;
	
	@Getter
	private UUID uuid;
	
	@Getter(AccessLevel.PROTECTED)
	private File sandboxDir;
	
	@Getter(AccessLevel.PROTECTED)
	private File binDir;
	
	@Getter(AccessLevel.PROTECTED)
	private File propFile;
	
	public AiRunner (@NonNull WorkerCommand cmd) throws IOException
	{
		aiId = cmd.getAiId();
		aiVersion = cmd.getVersion();
		game = cmd.getGame();
		uuid = cmd.getUuid();
		
		sandboxDir = Files.createTempDirectory("sandbox").toFile();
		sandboxDir.mkdirs();
	}
	
	public void retrieveAi ()
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			AiStartException, InterruptedException
	{
		File archive = new File(getSandboxDir(), "bin.tar.bz2");
		DatastoreFtpClient.retrieveAi(aiId, aiVersion, archive);
		
		binDir = new File(getSandboxDir(), "bin");
		binDir.mkdirs();
		ProcessBuilder pb = new ProcessBuilder("bsdtar", "xfj", archive.getAbsolutePath(), "-C", binDir
				.getAbsolutePath());
		Process p = pb.start();
		if (p.waitFor() != 0)
			throw new AiStartException(getAiId(), getAiVersion(), "Failed to extract Ai Binary.");
	}
	
	public void buildProperties () throws IOException
	{
//		propFile = new File(getSandboxDir(), "ai.prop");
		propFile = new File(getBinDir(), "ai.prop");
		Properties p = new Properties();
		p.put("turnierserver.worker.host", "::1"); // die systemeigenschaft ist
													// für das backend, nicht
													// für die sandboxen!
		p.put("turnierserver.worker.server.port", System.getProperty("turnierserver.worker.server.port"));
		p.put("turnierserver.worker.server.aichar", Character.toString(WorkerConnectionType.AI));
		p.put("turnierserver.serializer.compress.worker",
				System.getProperty("turnierserver.serializer.compress.sandbox"));
		p.put("turnierserver.ai.uuid", getUuid().toString());
		p.store(new FileWriter(propFile),
				"GENERATED FILE - DO NOT EDIT\nDiese Datei beinhaltet Informationen für die KI");
	}
	
	public int startAi () throws IOException, InterruptedException
	{
		ProcessBuilder pb = new ProcessBuilder("./start.sh", getPropFile().getAbsolutePath());
		pb.directory(getBinDir());
		System.out.println(pb.command());
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);
		Process p = pb.start();
		return p.waitFor();
	}
	
	public static void main (String args[])
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			AiStartException, InterruptedException
	{
		PropertyUtils.loadProperties("/home/dominic/git/Turnierserver-Config/turnierserver.prop");
		AiRunner air = new AiRunner(new WorkerCommand(WorkerCommand.STARTAI, 6, 1, 1, UUID.randomUUID()));
		System.out.println(air);
		System.out.println(air.getSandboxDir());
		air.retrieveAi();
		air.buildProperties();
		System.out.println("Starting ai …");
		System.out.println(air.startAi());
	}
}

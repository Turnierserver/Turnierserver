package org.pixelgaffer.turnierserver.codr.simulator;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.pixelgaffer.turnierserver.codr.Version;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;

@RequiredArgsConstructor
public class CodrAiWrapper implements Ai
{
	/** Das zugrundeliegende Spiel, enthält die Spiellogik. */
	@NonNull
	@Getter
	private CodrGameImpl game;
	
	/** Die UUID der KI im Netzwerk. */
	@Getter
	@NonNull
	private UUID uuid;
	
	/** Gibt an ob sich die KI schon verbunden hat. */
	@Getter
	private boolean connected = false;
	
	/** Der {@link CodrAiServerConnectionHandler} dieser KI. */
	@Getter
	private CodrAiServerConnectionHandler connectionHandler;
	
	/** Die Version der KI. */
	@Getter
	@Setter
	private Version version;
	
	/** Der ID-String dieser KI. */
	@Setter
	@Getter
	private String id;
	
	/** Der Index der KI in der Liste. */
	@Getter
	@Setter
	private int index;
	
	/** Hier kann die Spiellogik Informationen über die KI abspeichern. */
	@Getter
	@Setter
	private AiObject object;
	
	/** Der Process dieser KI. */
	@Getter
	private Process process;
	
	public void executeAi (String propertiesFile) throws IOException
	{
		ProcessBuilder pb = new ProcessBuilder((getVersion().executeCommand + " " + propertiesFile).split("\\s"));
		pb.directory(new File(Paths.versionBin(getVersion())));
		System.out.println(pb.directory() + "$ " + pb.command());
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);
		process = pb.start();
		ErrorLog.write("Die KI " + id + " wurde aufgerufen.");
	}
	
	public void receiveMessage (byte message[])
	{
		System.out.println("Empfange: " + new String(message));
		game.getLogic().receiveMessage(message, this);
	}
	
	@Override
	public void sendMessage (byte[] message) throws IOException
	{
		connectionHandler.send(message);
	}
	
	@Override
	public void disconnect () throws IOException
	{
		connectionHandler.close();
		getProcess().destroyForcibly();
	}
	
	public void connected (@NonNull CodrAiServerConnectionHandler handler)
	{
		connectionHandler = handler;
		connected = true;
		game.aiConnected();
	}
}

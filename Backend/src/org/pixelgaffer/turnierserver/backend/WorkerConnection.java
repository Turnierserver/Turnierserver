package org.pixelgaffer.turnierserver.backend;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import org.pixelgaffer.turnierserver.backend.server.BackendWorkerConnectionHandler;
import org.pixelgaffer.turnierserver.backend.workerclient.WorkerClient;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;

/**
 * Diese Klasse speichert Informationen über einen verbundenen Worker.
 */
@ToString(of = { "sandboxes", "usedSandboxes" })
public class WorkerConnection
{
	/**
	 * Die Anzahl der zur Verfügung stehenden Sandboxen.
	 */
	@Getter
	@Setter
	private int sandboxes;
	
	/** Die Anzahl der benutzten Sandboxen. */
	private int usedSandboxes = 0;
	
	/** Die Connection vom Backend zum Worker. */
	private BackendWorkerConnectionHandler connection;
	
	/** Die Connection vom Worker zum Backend. */
	private WorkerClient client;
	
	public WorkerConnection (@NonNull BackendWorkerConnectionHandler con, String addr, WorkerInfo info)
			throws IOException
	{
		connection = con;
		sandboxes = info.getSandboxes();
		client = new WorkerClient(addr, info);
	}
	
	/**
	 * Disconnected den Client zum Worker.
	 */
	public void disconnectClient ()
	{
		client.disconnect();
	}
	
	/**
	 * Gibt an, ob der Worker gerade Aufträge annehmen kann oder ob er
	 * ausgelastet ist.
	 */
	public boolean isAvailable ()
	{
		//return (usedSandboxes < sandboxes);
		System.out.println("todo:WorkerConnection:51: die istavailable-method ist iwi net auf die sandboxen und den worker angepasst");
		return true;
	}
	
	/**
	 * Schickt einen Kompilieren-Befehl an den Worker, ai enthält
	 * ${ai-id}v${ai-version}.
	 */
	public void compile (String ai, int game) throws IOException
	{
		String s[] = ai.split("v");
		if (s.length != 2)
			throw new IllegalArgumentException(ai);
		compile(Integer.parseInt(s[0]), Integer.parseInt(s[1]), game);
	}
	
	/**
	 * Schickt einen Kompilieren-Befehl an den Worker.
	 */
	public void compile (int aiId, int version, int game) throws IOException
	{
		connection.sendCommand(new WorkerCommand(WorkerCommand.COMPILE, aiId, version, game, UUID.randomUUID()));
	}
	
	public synchronized boolean addJob (AiWrapper ai)
	{
		if (!isAvailable())
			return false;
		return true;
	}
}

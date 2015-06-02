package org.pixelgaffer.turnierserver.backend;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import org.pixelgaffer.turnierserver.backend.server.BackendWorkerConnectionHandler;
import org.pixelgaffer.turnierserver.backend.workerclient.WorkerClient;
import org.pixelgaffer.turnierserver.networking.messages.MessageForward;
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
		// return (usedSandboxes < sandboxes);
		System.out.println(
				"todo:WorkerConnection:51: die istavailable-method ist iwi net auf die sandboxen und den worker angepasst");
		return true;
	}
	
	/**
	 * Schickt einen Kompilieren-Befehl an den Worker, ai enthält
	 * ${ai-id}v${ai-version}.
	 * @return 
	 */
	public WorkerCommand compile (String ai, int game) throws IOException
	{
		String s[] = ai.split("v");
		if (s.length != 2)
			throw new IllegalArgumentException(ai);
		return compile(Integer.parseInt(s[0]), Integer.parseInt(s[1]), game);
	}
	
	/**
	 * Schickt einen Kompilieren-Befehl an den Worker.
	 * @return 
	 */
	public WorkerCommand compile (int aiId, int version, int game) throws IOException
	{
		WorkerCommand cmd = new WorkerCommand(WorkerCommand.COMPILE, aiId, version, game, UUID.randomUUID());
		connection.sendCommand(cmd);
		return cmd;
	}
	
	/**
	 * Schickt einen StarteKI-Befehl an den Worker insofern dieser nicht
	 * komplett beschäftigt ist.
	 */
	public boolean addJob (AiWrapper ai, int game) throws IOException
	{
		if (!isAvailable())
			return false;
		connection.sendCommand(new WorkerCommand(WorkerCommand.STARTAI,
				ai.getId(), ai.getVersion(), game, ai.getUuid()));
		return true;
	}
	
	/**
	 * Schickt einen TerminiereKI-Befehl an den Worker.
	 */
	public void terminateJob (AiWrapper ai) throws IOException
	{
		connection.sendCommand(new WorkerCommand(WorkerCommand.TERMAI,
				ai.getId(), ai.getVersion(), -1, ai.getUuid()));
	}
	
	/**
	 * Schickt einen TerminiereKI-Befehl an den Worker.
	 */
	public void killJob (AiWrapper ai) throws IOException
	{
		connection.sendCommand(new WorkerCommand(WorkerCommand.KILLAI,
				ai.getId(), ai.getVersion(), -1, ai.getUuid()));
	}
	
	/**
	 * Schickt den MessageForward an den Worker weiter.
	 */
	public void sendMessage (MessageForward mf) throws IOException
	{
		client.sendMessage(mf);
	}
}

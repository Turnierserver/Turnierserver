package org.pixelgaffer.turnierserver.worker;

import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.KILL_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.RUN_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.TERM_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxMessage.FINISHED_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxMessage.KILLED_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxMessage.STARTED_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxMessage.TERMINATED_AI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.SANDBOX;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

import org.pixelgaffer.turnierserver.networking.messages.SandboxCommand;
import org.pixelgaffer.turnierserver.networking.messages.SandboxMessage;
import org.pixelgaffer.turnierserver.worker.server.WorkerConnectionHandler;

/**
 * Repräsentiert eine Sandbox.
 */
@ToString
public class Sandbox
{
	/** Gibt an ob die Sandbox busy ist. */
	@Getter
	private boolean busy = false;
	
	/** Die UUID des aktuell in der Sandbox ausgeführten Jobs. */
	private UUID currentJob;
	
	/** Die Connection von der Sandbox zum Worker. */
	@Getter
	private WorkerConnectionHandler connection;
	
	public Sandbox (WorkerConnectionHandler connectionHandler)
	{
		if (connectionHandler.getType().getType() != SANDBOX)
			throw new IllegalArgumentException();
		connection = connectionHandler;
	}
	
	/**
	 * Schickt den Job an die Sandbox. Dabei wird vorrausgesetzt, dass die
	 * Sandbox nicht beschäftigt ist. Gibt bei Erfolg true zurück, ansonsten
	 * false.
	 */
	public synchronized boolean sendJob (SandboxCommand job) throws IOException
	{
		System.out.println("Sandbox::sendJob(" + job + ")");
		if (job.getCommand() == RUN_AI)
		{
			if (isBusy())
				return false;
			busy = true;
		}
		else if ((job.getCommand() == KILL_AI) || (job.getCommand() == TERM_AI))
			busy = false;
		currentJob = job.getUuid();
		connection.sendJob(job);
		return true;
	}
	
	/**
	 * Empfängt die Antwort der Sandbox.
	 */
	public synchronized void sandboxAnswer (SandboxMessage answer)
	{
		switch (answer.getEvent())
		{
			case TERMINATED_AI:
			case KILLED_AI:
			case FINISHED_AI:
				try
				{
					WorkerMain.getBackendClient().sendSandboxMessage(answer);
				}
				catch (IOException e)
				{
					WorkerMain.getLogger().severe("Sandbox: Fehler beim notifien des Backends (" + answer + "): " + e);
					e.printStackTrace();
				}
				busy = false;
				break;
			case STARTED_AI:
				System.out.println("todo:Sandbox:89: Hier sollte ich mir überlegen ob ich iwas notifien soll");
				busy = true;
				break;
			default:
				WorkerMain.getLogger().severe("Sandbox: Unknown event received:" + answer);
				break;
		}
	}
	
	/**
	 * Wird aufgerufen, wenn sich die Sandbox disconnected hat.
	 */
	public void disconnected ()
	{
		sandboxAnswer(new SandboxMessage(TERMINATED_AI, currentJob));
	}
}

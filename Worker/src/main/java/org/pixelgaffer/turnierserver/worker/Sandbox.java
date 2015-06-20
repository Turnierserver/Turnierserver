package org.pixelgaffer.turnierserver.worker;

import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.SANDBOX;
import static org.pixelgaffer.turnierserver.worker.server.SandboxCommand.KILL_AI;
import static org.pixelgaffer.turnierserver.worker.server.SandboxCommand.RUN_AI;
import static org.pixelgaffer.turnierserver.worker.server.SandboxCommand.TERM_AI;
import static org.pixelgaffer.turnierserver.worker.server.SandboxMessage.FINISHED_AI;
import static org.pixelgaffer.turnierserver.worker.server.SandboxMessage.KILLED_AI;
import static org.pixelgaffer.turnierserver.worker.server.SandboxMessage.STARTED_AI;
import static org.pixelgaffer.turnierserver.worker.server.SandboxMessage.TERMINATED_AI;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.pixelgaffer.turnierserver.worker.server.SandboxCommand;
import org.pixelgaffer.turnierserver.worker.server.SandboxMessage;
import org.pixelgaffer.turnierserver.worker.server.WorkerConnectionHandler;

/**
 * Repräsentiert eine Sandbox.
 */
@ToString
public class Sandbox
{
	/** Gibt an ob die Sandbox busy ist. */
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private boolean busy = false;
	
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
			setBusy(true);
		}
		else if ((job.getCommand() == KILL_AI) || (job.getCommand() == TERM_AI))
			setBusy(false);
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
				System.out.println("Sandbox:62: Hier sollte ich das Backend informieren");
			case KILLED_AI:
				System.out.println("Sandbox:64: Hier sollte ich mir überlegen ob das einen Unterschied macht");
			case FINISHED_AI:
				System.out.println("Sandbox:66: Hier sollte ich Backend und Spiellogik informieren");
				setBusy(false);
				break;
			case STARTED_AI:
				System.out.println("Sandbox:70: Hier sollte ich mir überlegen ob ich iwas notifien soll");
				setBusy(true);
				break;
			default:
				WorkerMain.getLogger().severe("Sandbox: Unknown event received:" + answer);
				break;
		}
	}
}

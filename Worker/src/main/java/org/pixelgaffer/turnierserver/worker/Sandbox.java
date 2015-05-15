package org.pixelgaffer.turnierserver.worker;

import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.SANDBOX;
import lombok.Getter;
import lombok.Setter;

import org.pixelgaffer.turnierserver.networking.messages.StartAi;
import org.pixelgaffer.turnierserver.worker.server.WorkerConnectionHandler;

/**
 * Repräsentiert eine Sandbox.
 */
public class Sandbox
{
	/** Gibt an ob die Sandbox busy ist. */
	@Getter
	@Setter
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
	
	public void submitJob (StartAi job)
	{
		throw new UnsupportedOperationException();
	}
}

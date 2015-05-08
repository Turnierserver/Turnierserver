package org.pixelgaffer.turnierserver.backend;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.msgpack.annotation.Message;
import org.pixelgaffer.turnierserver.backend.server.BackendConnectionHandler;

/**
 * Diese Klasse speichert Informationen über einen verbundenen Worker.
 */
@RequiredArgsConstructor
@Message
public class WorkerConnection
{
	/**
	 * Die Anzahl der zur Verfügung stehenden Sandboxen.
	 */
	@Getter
	@Setter
	@NonNull
	private int sandboxes;
	
	/** Die Anzahl der benutzten Sandboxen. */
	private int usedSandboxes = 0;
	
	/** Die Connection zum Worker. */
	@NonNull
	private BackendConnectionHandler connection;
	
	/**
	 * Gibt an, ob der Worker gerade Aufträge annehmen kann oder ob er
	 * ausgelastet ist.
	 */
	public boolean isAvailable ()
	{
		return (usedSandboxes < sandboxes);
	}
	
	public synchronized boolean addJob (AiWrapper ai)
	{
		if (!isAvailable())
			return false;
		return true;
	}
}

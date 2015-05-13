package org.pixelgaffer.turnierserver.backend;

import java.io.IOException;
import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.pixelgaffer.turnierserver.backend.server.BackendWorkerConnectionHandler;
import org.pixelgaffer.turnierserver.backend.workerclient.WorkerClient;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;

/**
 * Diese Klasse speichert Informationen über einen verbundenen Worker.
 */
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
	
	public WorkerConnection (@NonNull BackendWorkerConnectionHandler con, InetSocketAddress addr, WorkerInfo info) throws IOException
	{
		connection = con;
		sandboxes = info.getSandboxes();
		
	}
	
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

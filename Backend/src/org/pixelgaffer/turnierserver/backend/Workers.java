package org.pixelgaffer.turnierserver.backend;

import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Diese Singleton-Klasse speichert alle verfügbaren Worker ab.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Workers
{
	private static Workers workers = null;
	
	private static Workers getInstance ()
	{
		if (workers == null)
			workers = new Workers();
		return workers;
	}
	
	public static void addWorker (@NonNull WorkerConnection worker)
	{
		getInstance().workerConnections.add(worker);
	}
	
	private Set<WorkerConnection> workerConnections = new HashSet<>();
}

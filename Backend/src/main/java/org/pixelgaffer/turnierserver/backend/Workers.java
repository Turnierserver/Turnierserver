package org.pixelgaffer.turnierserver.backend;

import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Diese Klasse speichert alle verfügbaren Worker ab.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Workers
{
	public static boolean addWorker (@NonNull WorkerConnection worker)
	{
		BackendMain.getLogger().info("Workers: addWorker(" + worker + ") called");
		return workerConnections.add(worker);
	}
	
	public static boolean removeWorker (WorkerConnection worker)
	{
		return workerConnections.remove(worker);
	}
	
	public static WorkerConnection getAvailableWorker ()
	{
		while (true)
		{
			synchronized (workerConnections)
			{
				for (WorkerConnection worker : workerConnections)
					if (worker.isAvailable())
						return worker;
				System.out.println("todo:Workers:37: hier muss iwi gewartet werden");
				return null;
			}
		}
	}
	
	private static final Set<WorkerConnection> workerConnections = new HashSet<>();
}

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
	/**
	 * Diese Methode registriert einen neuen Worker.
	 */
	public static boolean registerWorker (@NonNull WorkerConnection worker)
	{
		BackendMain.getLogger().info("Workers: Neuer Worker registriert: " + worker);
		boolean success = workerConnections.add(worker);
		if (success)
		{
			synchronized (workerConnections)
			{
				workerConnections.notifyAll();
			}
		}
		return success;
	}
	
	/**
	 * Diese Methode deregistriert einen neuen Worker.
	 */
	public static boolean removeWorker (WorkerConnection worker)
	{
		synchronized (workerConnections)
		{
			System.out.println("Workers:34: Hier muss iwi abgefangen werden wenn ein Worker crasht");
			return workerConnections.remove(worker);
		}
	}
	
	/**
	 * Gibt einen Worker mit mindestens einer unbeschäftigten Sandbox zurück.
	 * Diese Methode blockiert, bis ein solcher Worker verfügbar ist.
	 */
	public static WorkerConnection getStartableWorker ()
	{
		while (true)
		{
			synchronized (workerConnections)
			{
				for (WorkerConnection worker : workerConnections)
					if (worker.canStartAi())
						return worker;
				try
				{
					workerConnections.wait();
				}
				catch (InterruptedException e)
				{
					BackendMain.getLogger().warning("Workers: Beim Warten auf einen verfügbaren Worker: " + e);
				}
			}
		}
	}
	
	/**
	 * Gibt einen Worker zurück, der gerade keine KI kompiliert. Diese Methode
	 * blockiert, bis ein solcher Worker verfügbar ist.
	 */
	public static WorkerConnection getCompilableWorker ()
	{
		while (true)
		{
			synchronized (workerConnections)
			{
				for (WorkerConnection worker : workerConnections)
					if (!worker.isCompiling())
						return worker;
				try
				{
					workerConnections.wait();
				}
				catch (InterruptedException e)
				{
					BackendMain.getLogger().warning("Workers: Beim Warten auf einen verfügbaren Worker: " + e);
				}
			}
		}
	}
	
	/**
	 * Muss von jeder WorkerConnection aufgerufen werden, sobald der Worker
	 * (wieder) verfügbar ist.
	 */
	public static void workerIsAvailable ()
	{
		synchronized (workerConnections)
		{
			workerConnections.notifyAll();
		}
	}
	
	/**
	 * Beendet alle Verbindungen zu den Workern.
	 */
	public static void shutdown ()
	{
		synchronized (workerConnections)
		{
			workerConnections.forEach((con) -> con.disconnect());
			workerConnections.clear();
		}
	}
	
	private static final Set<WorkerConnection> workerConnections = new HashSet<>();
}

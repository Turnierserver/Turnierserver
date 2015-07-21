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
	private static final Object lock = new Workers();
	
	/**
	 * Diese Methode registriert einen neuen Worker.
	 */
	public static boolean registerWorker (@NonNull WorkerConnection worker)
	{
		BackendMain.getLogger().info("Neuer Worker registriert: " + worker);
		boolean success;
		synchronized (workerConnections)
		{
			success = workerConnections.add(worker);
		}
		if (success)
		{
			synchronized (lock)
			{
				lock.notifyAll();
			}
		}
		return success;
	}
	
	/**
	 * Diese Methode deregistriert einen neuen Worker.
	 */
	public static boolean removeWorker (WorkerConnection worker)
	{
		BackendMain.getLogger().info("Workers: Entferne Worker " + worker);
		synchronized (workerConnections)
		{
			Jobs.workerDisconnected(worker);
			return workerConnections.remove(worker);
		}
	}
	
	/**
	 * Gibt einen Worker mit mindestens einer unbeschäftigten Sandbox zurück.
	 * Diese Methode blockiert, bis ein solcher Worker verfügbar ist.
	 */
	public static WorkerConnection getStartableWorker (String lang)
	{
		while (true)
		{
			BackendMain.getLogger().debug("Searching for a startable worker for the language " + lang);
			synchronized (workerConnections)
			{
				for (WorkerConnection worker : workerConnections)
				{
					if (worker.canStartAi(lang))
					{
						BackendMain.getLogger().debug(
								"Found a startable worker for the language " + lang + ": " + worker);
						return worker;
					}
				}
			}
			BackendMain.getLogger().debug("Waiting for a startable worker for the language " + lang);
			synchronized (lock)
			{
				try
				{
					lock.wait();
				}
				catch (InterruptedException e)
				{
					BackendMain.getLogger().warning("Exception beim Warten auf einen verfügbaren Worker: " + e);
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
			}
			synchronized (lock)
			{
				try
				{
					lock.wait();
				}
				catch (InterruptedException e)
				{
					BackendMain.getLogger().warning("Exception beim Warten auf einen verfügbaren Worker: " + e);
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
		synchronized (lock)
		{
			lock.notifyAll();
		}
	}
	
	/**
	 * Beendet alle Verbindungen zu den Workern.
	 */
	public static void shutdown ()
	{
		synchronized (workerConnections)
		{
			workerConnections.forEach( (con) -> con.disconnect());
			workerConnections.clear();
		}
	}
	
	private static final Set<WorkerConnection> workerConnections = new HashSet<>();
}

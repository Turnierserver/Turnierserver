package org.pixelgaffer.turnierserver.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.pixelgaffer.turnierserver.worker.server.SandboxCommand;

/**
 * Diese Klasse speichert alle verbundenen Sandboxen.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Sandboxes
{
	private static List<Sandbox> sandboxes = new ArrayList<>();
	
	/**
	 * Fügt die Sandbox zur Liste hinzu.
	 */
	public static boolean addSandbox (Sandbox sandbox)
	{
		boolean success = sandboxes.add(sandbox);
		WorkerMain.workerInfo.setSandboxes(sandboxes.size());
		try
		{
			WorkerMain.notifyInfoUpdated();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * Entfernt die Sandbox aus der Liste.
	 */
	public static boolean removeSandbox (Sandbox sandbox)
	{
		boolean success = sandboxes.remove(sandbox);
		WorkerMain.workerInfo.setSandboxes(sandboxes.size());
		try
		{
			WorkerMain.notifyInfoUpdated();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * Schickt den Job an die KI.
	 */
	public static boolean send (SandboxCommand job) throws IOException
	{
		for (Sandbox s : sandboxes)
		{
			System.out.println(
					"todo:Sandboxes:43: hier wärs schön wenn die supporteten sprachen geprüft werden würden");
			if (!s.isBusy())
			{
				s.sendJob(job);
				return true;
			}
		}
		return false;
	}
}

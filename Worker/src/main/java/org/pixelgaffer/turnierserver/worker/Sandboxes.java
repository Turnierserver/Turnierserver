package org.pixelgaffer.turnierserver.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.pixelgaffer.turnierserver.networking.messages.SandboxCommand;

/**
 * Diese Klasse speichert alle verbundenen Sandboxen.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Sandboxes
{
	private static final List<Sandbox> sandboxes = new ArrayList<>();
	
	public static final Map<UUID, Sandbox> sandboxJobs = new HashMap<>();
	
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
	 * Entfernt die Sandbox aus der Liste. Ruft außerdem
	 * {@link Sandbox#disconnected()} auf.
	 */
	public static boolean removeSandbox (Sandbox sandbox)
	{
		WorkerMain.getLogger().info("Die Sandbox " + sandbox + " hat sich disconnected");
		sandbox.disconnected();
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
	 * Schickt den Job an eine Sandbox und gibt diese zurück. Wenn keine Sandbox
	 * unbeschäftigt ist, wird null zurückgegeben. DIESE METHODE WARTET NICHT
	 * AUF EINE FREIE SANDBOX!
	 */
	public static Sandbox send (SandboxCommand job) throws IOException
	{
		for (Sandbox s : sandboxes)
		{
			System.out.println(
					"todo:Sandboxes:43: hier wärs schön wenn die supporteten sprachen geprüft werden würden");
			if (!s.isBusy())
			{
				if (s.sendJob(job))
					return s;
			}
		}
		return null;
	}
}

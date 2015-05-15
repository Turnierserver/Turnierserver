package org.pixelgaffer.turnierserver.worker;

import java.util.ArrayList;
import java.util.List;

import org.pixelgaffer.turnierserver.networking.messages.StartAi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
		return sandboxes.add(sandbox);
	}
	
	/**
	 * Entfernt die Sandbox aus der Liste.
	 */
	public static boolean removeSandbox (Sandbox sandbox)
	{
		return sandboxes.remove(sandbox);
	}
	
	public static boolean submitJob (StartAi job)
	{
		for (Sandbox s : sandboxes)
		{
			if (!s.isBusy())
			{
				s.submitJob(job);
				return true;
			}
		}
		return false;
	}
}

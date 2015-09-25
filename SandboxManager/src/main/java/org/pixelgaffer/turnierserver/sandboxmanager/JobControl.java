/*
 * JobControl.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pixelgaffer.turnierserver.sandboxmanager;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;
import org.pixelgaffer.turnierserver.Airbrake;
import lombok.Getter;

public class JobControl
{
	@Getter
	private boolean active = true;
	
	private final Deque<Job> queue = new LinkedList<>();
	@Getter
	private AiExecutor current = null;
	
	/**
	 * Shuts this JobControl down. Needs to be called before the system exits.
	 */
	public void shutdown ()
	{
		SandboxMain.getLogger().info("Shutting down the JobControll");
		active = false;
		synchronized (queue)
		{
			queue.clear();
			if (current != null)
				current.terminateAi();
		}
	}
	
	public void doJob (Job job)
	{
		try
		{
			current = new AiExecutor(job, this);
		}
		catch (Exception e)
		{
			Airbrake.log(e).printStackTrace();
			jobFinished(job.getUuid());
			SandboxMain.getClient().sendMessage(job.getUuid(), 'T');
		}
		new Thread(current, "AiExecutor-Thread").start();
	}
	
	public void addJob (Job job)
	{
		synchronized (queue)
		{
			if (current == null)
				doJob(job);
			else
				queue.offerLast(job);
		}
	}
	
	public void terminateJob (UUID uuid)
	{
		synchronized (queue)
		{
			if ((current != null) && (current.getJob().getUuid().equals(uuid)))
				current.terminateAi();
			else
				while (queue.remove(new Job(uuid)));
		}
	}
	
	public void killJob (UUID uuid)
	{
		synchronized (queue)
		{
			if ((current != null) && (current.getJob().getUuid().equals(uuid)))
				current.killAi();
			else
				while (queue.remove(new Job(uuid)));
		}
	}
	
	public void jobFinished (UUID uuid)
	{
		if (current.getJob().getUuid().equals(uuid))
		{
			if (queue.isEmpty())
			{
				SandboxMain.getLogger().debug("Habe aktuell keinen Auftrag, setze current auf null");
				current = null;
			}
			else
				doJob(queue.pollFirst());
		}
		else
			SandboxMain.getLogger()
					.warning("Aktueller Job ist " + current.getJob().getUuid() + " aber " + uuid + " wurde beendet");
	}
}

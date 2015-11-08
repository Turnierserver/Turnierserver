/*
 * Sandbox.java
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
package org.pixelgaffer.turnierserver.worker;

import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.CPU_TIME;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.KILL_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.RUN_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.TERM_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxMessage.FINISHED_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxMessage.KILLED_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxMessage.STARTED_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxMessage.TERMINATED_AI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.SANDBOX;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.pixelgaffer.turnierserver.Airbrake;
import org.pixelgaffer.turnierserver.networking.messages.SandboxCommand;
import org.pixelgaffer.turnierserver.networking.messages.SandboxMessage;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo.SandboxInfo;
import org.pixelgaffer.turnierserver.worker.server.WorkerConnectionHandler;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * Repräsentiert eine Sandbox.
 */
@ToString(exclude = { "connection", "semaphore", "cpuTimeLock" })
public class Sandbox
{
	@Getter
	private SandboxInfo sandboxInfo = new SandboxInfo();
	
	@Getter
	private long lastCpuTime;
	private Semaphore semaphore = new Semaphore(1, true);
	private Object cpuTimeLock = new Object();
	
	public void updateCpuTime ()
	{
		Thread sendJob = new Thread( () -> {
			try
			{
				while (semaphore.tryAcquire(500, TimeUnit.MICROSECONDS))
				{
					semaphore.release();
				}
				sendJob(new SandboxCommand(CPU_TIME, -1, -1, -1, "", null, -1));
			}
			catch (Exception e)
			{
				Airbrake.log(e).printStackTrace();
			}
		});
//		WorkerMain.getLogger().debug(
//				"Gehe in Synchronized in update " + currentJob + " in thread " + Thread.currentThread());
		synchronized (cpuTimeLock)
		{
			try
			{
//				WorkerMain.getLogger().debug(
//						"Warte auf notify in uuid " + currentJob + " in thread " + Thread.currentThread());
				sendJob.start();
				semaphore.acquire();
				cpuTimeLock.wait();
				semaphore.release();
//				WorkerMain.getLogger().debug(
//						"Wurde notified in uuid " + currentJob + " in thread " + Thread.currentThread());
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
//		WorkerMain.getLogger().debug(
//				"Gehe aus Synchronized in update " + currentJob + " in thread " + Thread.currentThread());
	}
	
	public long getCpuTimeDiff ()
	{
		long oldTime = lastCpuTime;
		updateCpuTime();
		return Math.max(lastCpuTime - oldTime, 0);
	}
	
	private void setBusy (boolean busy)
	{
		if (isBusy() != busy)
		{
			sandboxInfo.setBusy(busy);
			try
			{
				WorkerMain.notifyInfoUpdated();
			}
			catch (IOException e)
			{
				WorkerMain.getLogger().critical("Failed to notify Backend that the Worker changed: " + e);
			}
		}
	}
	
	public boolean isBusy ()
	{
		return sandboxInfo.isBusy();
	}
	
	public void setLangs (@NonNull Set<String> langs)
	{
		if (!langs.equals(getLangs()))
		{
			sandboxInfo.setLangs(langs);
			try
			{
				WorkerMain.notifyInfoUpdated();
			}
			catch (IOException e)
			{
				WorkerMain.getLogger().critical("Failed to notify Backend that the Worker changed: " + e);
			}
		}
	}
	
	public Set<String> getLangs ()
	{
		return sandboxInfo.getLangs();
	}
	
	/** Die UUID des aktuell in der Sandbox ausgeführten Jobs. */
	private UUID currentJob;
	
	/** Die Connection von der Sandbox zum Worker. */
	@Getter
	private WorkerConnectionHandler connection;
	
	public Sandbox (WorkerConnectionHandler connectionHandler)
	{
		if (connectionHandler.getType().getType() != SANDBOX)
			throw new IllegalArgumentException();
		connection = connectionHandler;
	}
	
	/**
	 * Schickt den Job an die Sandbox. Dabei wird vorrausgesetzt, dass die
	 * Sandbox nicht beschäftigt ist. Gibt bei Erfolg true zurück, ansonsten
	 * false.
	 */
	public synchronized boolean sendJob (SandboxCommand job) throws IOException
	{
		WorkerMain.getLogger().debug("Sende " + job);
		if (job.getCommand() == RUN_AI)
		{
			if (isBusy())
				return false;
			setBusy(true);
		}
		else if ((job.getCommand() == KILL_AI) || (job.getCommand() == TERM_AI))
			setBusy(false);
		currentJob = job.getUuid();
		connection.sendJob(job);
		return true;
	}
	
	/**
	 * Empfängt die Antwort der Sandbox.
	 */
	public synchronized void sandboxAnswer (SandboxMessage answer)
	{
		switch (answer.getEvent())
		{
			case TERMINATED_AI:
			case KILLED_AI:
			case FINISHED_AI:
				try
				{
					Sandboxes.releaseIsolateBoxid(answer.getUuid());
				}
				catch (Exception e)
				{
					WorkerMain.getLogger().critical("Fehler beim releasen der isolate boxid von " + answer.getUuid());
					Airbrake.log(e).printStackTrace();
				}
				try
				{
					WorkerMain.getLogger().info("Die KI " + answer.getUuid() + " hat sich mit " + answer.getEvent() + " beendet");
					WorkerMain.getBackendClient().sendSandboxMessage(answer);
				}
				catch (IOException e)
				{
					WorkerMain.getLogger().critical("Fehler beim notifien des Backends (" + answer + "): " + e);
					Airbrake.log(e).printStackTrace();
				}
				setBusy(false);
				break;
			case STARTED_AI:
				WorkerMain.getLogger().todo("Hier sollte ich mir überlegen ob ich iwas notifien soll");
				setBusy(true);
				break;
			case CPU_TIME:
				lastCpuTime = answer.getCpuTime();
//				WorkerMain.getLogger().debug(
//						"Gehe in Synchronized in " + currentJob + " in thread " + Thread.currentThread());
				synchronized (cpuTimeLock)
				{
//					WorkerMain.getLogger().debug("Notify " + currentJob + " in thread " + Thread.currentThread());
					cpuTimeLock.notifyAll();
//					WorkerMain.getLogger().debug("Notified " + currentJob + " in thread " + Thread.currentThread());
				}
//				WorkerMain.getLogger().debug(
//						"Gehe aus Synchronized in " + currentJob + " in thread " + Thread.currentThread());
				break;
			default:
				WorkerMain.getLogger().critical("Unknown event received:" + answer);
				break;
		}
	}
	
	/**
	 * Wird aufgerufen, wenn sich die Sandbox disconnected hat.
	 */
	public void disconnected ()
	{
		sandboxAnswer(new SandboxMessage(TERMINATED_AI, currentJob));
	}
}

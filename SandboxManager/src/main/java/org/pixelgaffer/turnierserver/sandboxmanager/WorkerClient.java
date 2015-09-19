/*
 * WorkerClient.java
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.PropertyUtils.RECON_IVAL;
import static org.pixelgaffer.turnierserver.PropertyUtils.getIntRequired;
import static org.pixelgaffer.turnierserver.PropertyUtils.getStringRequired;
import static org.pixelgaffer.turnierserver.networking.NetworkService.getService;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.CPU_TIME;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.KILL_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.RUN_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.TERM_AI;
import static org.pixelgaffer.turnierserver.sandboxmanager.SandboxMain.commands;
import java.io.IOException;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.Airbrake;
import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.PropertyUtils;
import org.pixelgaffer.turnierserver.networking.NetworkService;
import org.pixelgaffer.turnierserver.networking.messages.SandboxCommand;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;
import lombok.Getter;
import naga.NIOSocket;
import naga.SocketObserver;

public class WorkerClient implements SocketObserver
{
	NIOSocket client;
	@Getter
	private String host;
	@Getter
	private int port;
	
	@Getter
	private boolean connected;
	
	private final DataBuffer buf = new DataBuffer();
	
	@Getter
	private final JobControl jobControl = new JobControl();
	
	public WorkerClient () throws IOException
	{
		host = getStringRequired("worker.host");
		port = getIntRequired("worker.port");
		
		client = getService().openSocket(host, port);
		client.listen(this);
	}
	
	public void sendMessage (UUID uuid, char event)
	{
		JSONObject json = new JSONObject();
		json.put("uuid", uuid);
		json.put("event", Character.toString(event));
		client.write((json + "\n").getBytes(UTF_8));
	}
	
	public void sendMessage (UUID uuid, char event, long cpuTime)
	{
		JSONObject json = new JSONObject();
		json.put("uuid", uuid);
		json.put("event", Character.toString(event));
		json.put("cpuTime", cpuTime);
		client.write((json + "\n").getBytes(UTF_8));
	}
	
	@Override
	public void connectionOpened (NIOSocket socket)
	{
		SandboxMain.getLogger().info("Mit dem Worker verbunden");
		connected = true;
		
		JSONArray langs = new JSONArray(commands.keySet().toArray());
		client.write(("S" + langs + "\n").getBytes(UTF_8));
	}
	
	private final Runnable reconnector = () -> {
		int interval = PropertyUtils.getInt(RECON_IVAL, 3000);
		while (!connected)
		{
			try
			{
				client = NetworkService.getService().openSocket(host, port);
				client.listen(this);
			}
			catch (IOException e)
			{
				Airbrake.log(e).printStackTrace();
			}
			
			try
			{
				Thread.sleep(interval);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		reconnectorRunning = false;
	};
	
	private boolean reconnectorRunning = false;
	
	@Override
	public void connectionBroken (NIOSocket socket, Exception e)
	{
		SandboxMain.getLogger().critical("Verbindung zum Worker kaputt" + (e == null ? "" : ": " + e));
		connected = false;
		synchronized (this)
		{
			if (!reconnectorRunning)
				new Thread(reconnector, "Reconnector").start();
			reconnectorRunning = true;
		}
	}
	
	@Override
	public void packetReceived (NIOSocket socket, byte[] packet)
	{
		SandboxMain.getLogger().debug("Packet empfangen");
		buf.add(packet);
		byte line[];
		while ((line = buf.readLine()) != null)
		{
			try
			{
				SandboxCommand cmd = Parsers.getSandbox().parse(line, SandboxCommand.class);
				switch (cmd.getCommand()) {
					case RUN_AI:
						SandboxMain.getLogger().info("Auftrag erhalten: Run AI "
								+ cmd.getId() + "v" + cmd.getVersion() + " " + cmd.getUuid() + " " + cmd.getLang());
						jobControl.addJob(new Job(cmd.getId(), cmd.getVersion(), cmd.getLang(), cmd.getUuid(),
								cmd.getMaxRuntime()));
						break;
						
					case TERM_AI:
						SandboxMain.getLogger().info("Auftrag erhalten: Terminate AI " + cmd.getUuid());
						jobControl.terminateJob(cmd.getUuid());
						break;
						
					case KILL_AI:
						SandboxMain.getLogger().info("Auftrag erhalten: Kill AI " + cmd.getUuid());
						jobControl.killJob(cmd.getUuid());
						break;
						
					case CPU_TIME:
						SandboxMain.getLogger().info("Auftrag erhalten: CPU-Time herausfinden");
					{
						long time = CpuTimer.getCpuTime(jobControl.getCurrent().getBoxid());
						SandboxMain.getLogger().debug(time);
						sendMessage(jobControl.getCurrent().getJob().getUuid(), 'C', time);
					}
						break;
					default:
						SandboxMain.getLogger().debug("Also es wäre schön wenn ich den Befehl " + cmd + " verstehen würde");
				}
			}
			catch (Exception e)
			{
				SandboxMain.getLogger().critical("Error while parsing: " + new String(line, UTF_8));
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void packetSent (NIOSocket arg0, Object arg1)
	{
	}
}

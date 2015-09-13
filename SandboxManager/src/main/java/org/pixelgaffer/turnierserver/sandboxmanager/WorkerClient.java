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
import static org.pixelgaffer.turnierserver.sandboxmanager.SandboxMain.commands;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.PropertyUtils;
import org.pixelgaffer.turnierserver.networking.NetworkService;
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
				e.printStackTrace();
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
			String linestr = new String(line, UTF_8);
			JSONObject json = new JSONObject(linestr);
			String cmd = json.getString("command");
			if (cmd.equals("R"))
			{
				int id = json.getInt("id");
				int version = json.getInt("version");
				String lang = json.getString("lang");
				UUID uuid = UUID.fromString(json.getString("uuid"));
				SandboxMain.getLogger().info("Auftrag erhalten: Run AI " + id + "v" + version + " " + uuid);
				jobControl.addJob(new Job(id, version, lang, uuid));
			}
			else if (cmd.equals("T"))
			{
				UUID uuid = UUID.fromString(json.getString("uuid"));
				SandboxMain.getLogger().info("Auftrag erhalten: Terminate AI " + uuid);
				
				jobControl.terminateJob(uuid);
			}
			else if (cmd.equals("K"))
			{
				UUID uuid = UUID.fromString(json.getString("uuid"));
				SandboxMain.getLogger().info("Auftrag erhalten: Kill AI " + uuid);
				
				jobControl.killJob(uuid);
			}
			else if (cmd.equals("C"))
			{
				SandboxMain.getLogger().info("Auftrag erhalten: CPU-Time herausfinden");
				
				long time = CpuTimer.getCpuTime(jobControl.getCurrent().getBoxid());
				SandboxMain.getLogger().debug(time);
				sendMessage(jobControl.getCurrent().getJob().getUuid(), 'C', time);
			}
			else
				SandboxMain.getLogger().debug("Also es wäre schön wenn ich den Befehl " + cmd + " verstehen würde");
		}
	}
	
	@Override
	public void packetSent (NIOSocket arg0, Object arg1)
	{
	}
}

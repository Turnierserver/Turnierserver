package org.pixelgaffer.turnierserver.sandboxmanager;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.PropertyUtils.RECON_IVAL;
import static org.pixelgaffer.turnierserver.PropertyUtils.getIntRequired;
import static org.pixelgaffer.turnierserver.PropertyUtils.getStringRequired;
import static org.pixelgaffer.turnierserver.networking.NetworkService.getService;
import static org.pixelgaffer.turnierserver.sandboxmanager.SandboxMain.commands;
import static org.pixelgaffer.turnierserver.sandboxmanager.SandboxMain.getLogger;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import naga.NIOSocket;
import naga.SocketObserver;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.PropertyUtils;
import org.pixelgaffer.turnierserver.networking.NetworkService;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

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
		json.append("uuid", uuid);
		json.append("event", event);
		client.write((json + "\n").getBytes(UTF_8));
	}
	
	@Override
	public void connectionOpened (NIOSocket socket)
	{
		getLogger().info("Mit dem Worker verbunden");
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
		getLogger().critical("Verbindung zum Worker kaputt" + (e == null ? "" : ": " + e));
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
		System.out.println("packet empfangen");
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
				getLogger().info("Auftrag erhalten: Run AI " + id + "v" + version + " " + uuid);
				
				jobControl.addJob(new Job(id, version, lang, uuid));
			}
			else if (cmd.equals("T"))
			{
				UUID uuid = UUID.fromString(json.getString("uuid"));
				getLogger().info("Auftrag erhalten: Terminate AI " + uuid);
				
				getLogger().todo("jobControl.terminateJob(uuid);");
			}
			else if (cmd.equals("K"))
			{
				UUID uuid = UUID.fromString(json.getString("uuid"));
				getLogger().info("Auftrag erhalten: Kill AI " + uuid);
				
				getLogger().todo("jobControl.killJob(uuid);");
			}
			else
				getLogger().debug("Also es wäre schön wenn ich den Befehl " + cmd + " verstehen würde");
		}
	}
	
	@Override
	public void packetSent (NIOSocket arg0, Object arg1)
	{
	}
}

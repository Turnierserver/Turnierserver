package org.pixelgaffer.turnierserver.worker.backendclient;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.PropertyUtils.RECON_IVAL;
import static org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine.AICONNECTED;
import static org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine.ANSWER;
import static org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine.INFO;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.COMPILE;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.KILLAI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.STARTAI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.TERMAI;
import static org.pixelgaffer.turnierserver.worker.server.SandboxCommand.KILL_AI;
import static org.pixelgaffer.turnierserver.worker.server.SandboxCommand.RUN_AI;
import static org.pixelgaffer.turnierserver.worker.server.SandboxCommand.TERM_AI;

import java.io.IOException;

import lombok.Getter;
import naga.NIOSocket;
import naga.SocketObserver;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.PropertyUtils;
import org.pixelgaffer.turnierserver.compile.Backend;
import org.pixelgaffer.turnierserver.networking.NetworkService;
import org.pixelgaffer.turnierserver.networking.bwprotocol.AiConnected;
import org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine;
import org.pixelgaffer.turnierserver.networking.bwprotocol.WorkerCommandAnswer;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;
import org.pixelgaffer.turnierserver.worker.Sandbox;
import org.pixelgaffer.turnierserver.worker.Sandboxes;
import org.pixelgaffer.turnierserver.worker.WorkerMain;
import org.pixelgaffer.turnierserver.worker.compile.CompileQueue;
import org.pixelgaffer.turnierserver.worker.server.SandboxCommand;

/**
 * Diese Klasse ist der Client zum Backend.
 */
public class BackendClient implements SocketObserver, Backend
{
	private NIOSocket client;
	@Getter
	private String ip;
	@Getter
	private int port;
	
	/** Speichert, ob der Client verbunden ist. */
	@Getter
	private boolean connected = false;
	
	private DataBuffer buf = new DataBuffer();
	
	public BackendClient (String ip, int port) throws IOException
	{
		this.ip = ip;
		this.port = port;
		client = NetworkService.getService().openSocket(ip, port);
		client.listen(this);
	}
	
	public void sendAnswer (WorkerCommandAnswer answer) throws IOException
	{
		client.write(new ProtocolLine(ANSWER, answer).serialize());
		client.write("\n".getBytes(UTF_8));
	}
	
	public void sendInfo (WorkerInfo info) throws IOException
	{
		client.write(new ProtocolLine(INFO, info).serialize());
		client.write("\n".getBytes(UTF_8));
	}
	
	public void sendAiConnected (AiConnected aiConnected) throws IOException
	{
		client.write(new ProtocolLine(AICONNECTED, aiConnected).serialize());
		client.write("\n".getBytes(UTF_8));
	}
	
	@Override
	public void connectionOpened (NIOSocket socket)
	{
		WorkerMain.getLogger().info("BackendClient: Established Connection to " + socket.getIp());
		connected = true;
		try
		{
			socket.write(Parsers.getWorker().parse(WorkerMain.workerInfo));
			socket.write("\n".getBytes(UTF_8));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private final Runnable reconnector = () -> {
		int interval = PropertyUtils.getInt(RECON_IVAL, 3000);
		while (!connected)
		{
			try
			{
				client = NetworkService.getService().openSocket(ip, port);
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
	public void connectionBroken (NIOSocket socket, Exception exception)
	{
		WorkerMain.getLogger().severe("BackendClient: Connection to Backend broken: " + exception);
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
		buf.add(packet);
		byte line[];
		while ((line = buf.readLine()) != null)
		{
			try
			{
				WorkerCommand cmd = Parsers.getWorker().parse(line, WorkerCommand.class);
				WorkerMain.getLogger().info("BackendClient: Empfangen: " + cmd);
				if (cmd.getAction() == COMPILE)
					CompileQueue.addJob(cmd);
				else if (cmd.getAction() == STARTAI)
				{
					try
					{
						SandboxCommand scmd = new SandboxCommand(RUN_AI,
								cmd.getAiId(), cmd.getVersion(), cmd.getUuid());
						Sandbox s = Sandboxes.send(scmd);
						if (s == null)
							System.out.println("todo:BackendClient:111: Hier sollte das Backend informiert werden.");
						else
							Sandboxes.sandboxJobs.put(cmd.getUuid(), s);
					}
					catch (Exception e)
					{
						WorkerMain.getLogger().severe("BackendClient: Fehler beim Senden des StartKI-Befehls: " + e);
						e.printStackTrace();
					}
				}
				else if (cmd.getAction() == TERMAI || cmd.getAction() == KILLAI)
				{
					try
					{
						SandboxCommand scmd = new SandboxCommand(cmd.getAction() == TERMAI ? TERM_AI : KILL_AI,
								cmd.getAiId(), cmd.getVersion(), cmd.getUuid());
						Sandbox s = Sandboxes.sandboxJobs.get(cmd.getUuid());
						if (s == null)
							WorkerMain.getLogger().severe(
									"Das Backend hat mich beauftragt die unbekannte KI " + cmd.getUuid()
											+ " zu beenden.");
						else
							s.sendJob(scmd);
					}
					catch (IOException ioe)
					{
						WorkerMain.getLogger().severe("BackendClient: Fehler beim Senden des StopKI-Befehls: " + ioe);
						ioe.printStackTrace();
					}
				}
				else
					WorkerMain.getLogger().severe("BackendClient: Unknown job " + cmd.getAction());
			}
			catch (Exception e)
			{
				WorkerMain.getLogger().severe("BackendClient: Failed to parse Command: " + e);
			}
		}
	}
	
	@Override
	public void packetSent (NIOSocket socket, Object tag)
	{
	}
}

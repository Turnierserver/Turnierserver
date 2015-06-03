package org.pixelgaffer.turnierserver.worker.backendclient;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine.ANSWER;
import static org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine.INFO;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.COMPILE;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.STARTAI;

import java.io.IOException;

import lombok.Getter;
import naga.NIOSocket;
import naga.SocketObserver;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.compile.Backend;
import org.pixelgaffer.turnierserver.networking.NetworkService;
import org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine;
import org.pixelgaffer.turnierserver.networking.bwprotocol.WorkerCommandAnswer;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;
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
	
	/** Speichert, ob der Client verbunden ist. */
	@Getter
	private boolean connected = false;
	
	private DataBuffer buf = new DataBuffer();
	
	public BackendClient (String ip, int port) throws IOException
	{
		client = NetworkService.getService().openSocket(ip, port);
		client.listen(this);
	}
	
	public void sendAnswer (WorkerCommandAnswer answer) throws IOException
	{
		System.out.println("BackendClient:39: sending success: " + answer);
		client.write(new ProtocolLine(ANSWER, answer).serialize());
		client.write("\n".getBytes(UTF_8));
	}
	
	public void sendInfo (WorkerInfo info) throws IOException
	{
		client.write(new ProtocolLine(INFO, info).serialize());
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
	
	@Override
	public void connectionBroken (NIOSocket socket, Exception exception)
	{
		WorkerMain.getLogger().severe("BackendClient: Connection to Backend broken: " + exception);
		connected = false;
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
				if (cmd.getAction() == COMPILE)
					CompileQueue.addJob(cmd);
				else if (cmd.getAction() == STARTAI)
				{
					// das Herunterladen & verschicken in einem neuen Thread
					// machen, damit der Netzwerk-Thread nicht zu lange
					// angehalten wird
					new Thread( () -> {
						try
						{
							SandboxCommand scmd = new SandboxCommand(SandboxCommand.RUN_AI,
									cmd.getAiId(), cmd.getVersion(), cmd.getUuid());
							Sandboxes.send(scmd);
						}
						catch (Exception e)
						{
							WorkerMain.getLogger().severe("BackendClient: Fehler beim Senden des StartKI-Befehls: " + e);
							e.printStackTrace();
						}
					}).start();
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

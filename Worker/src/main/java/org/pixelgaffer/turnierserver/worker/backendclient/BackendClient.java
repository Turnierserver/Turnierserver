/*
 * BackendClient.java
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
package org.pixelgaffer.turnierserver.worker.backendclient;

import static org.pixelgaffer.turnierserver.PropertyUtils.RECON_IVAL;
import static org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine.AICONNECTED;
import static org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine.ANSWER;
import static org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine.INFO;
import static org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine.SANDBOX_MESSAGE;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.KILL_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.RUN_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxCommand.TERM_AI;
import static org.pixelgaffer.turnierserver.networking.messages.SandboxMessage.TERMINATED_AI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.COMPILE;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.KILLAI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.STARTAI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerCommand.TERMAI;

import java.io.IOException;

import lombok.Getter;
import naga.NIOSocket;
import naga.SocketObserver;

import org.pixelgaffer.turnierserver.Airbrake;
import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.PropertyUtils;
import org.pixelgaffer.turnierserver.compile.Backend;
import org.pixelgaffer.turnierserver.networking.NetworkService;
import org.pixelgaffer.turnierserver.networking.bwprotocol.AiConnected;
import org.pixelgaffer.turnierserver.networking.bwprotocol.ProtocolLine;
import org.pixelgaffer.turnierserver.networking.bwprotocol.WorkerCommandAnswer;
import org.pixelgaffer.turnierserver.networking.messages.SandboxCommand;
import org.pixelgaffer.turnierserver.networking.messages.SandboxMessage;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;
import org.pixelgaffer.turnierserver.worker.Sandbox;
import org.pixelgaffer.turnierserver.worker.Sandboxes;
import org.pixelgaffer.turnierserver.worker.WorkerMain;
import org.pixelgaffer.turnierserver.worker.compile.CompileQueue;

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
		client.write(new ProtocolLine(ANSWER, answer).serialize(true));
	}
	
	public void sendInfo (WorkerInfo info) throws IOException
	{
		client.write(new ProtocolLine(INFO, info).serialize(true));
	}
	
	public void sendAiConnected (AiConnected aiConnected) throws IOException
	{
		client.write(new ProtocolLine(AICONNECTED, aiConnected).serialize(true));
	}
	
	public void sendSandboxMessage (SandboxMessage msg) throws IOException
	{
		client.write(new ProtocolLine(SANDBOX_MESSAGE, msg).serialize(true));
	}
	
	@Override
	public void connectionOpened (NIOSocket socket)
	{
		WorkerMain.getLogger().info("Verbunden mit " + socket.getIp());
		connected = true;
		try
		{
			socket.write(Parsers.getWorker().parse(WorkerMain.workerInfo, true));
		}
		catch (IOException e)
		{
			Airbrake.log(e).printStackTrace();
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
	public void connectionBroken (NIOSocket socket, Exception exception)
	{
		WorkerMain.getLogger().critical("Das Backend hat die Verbindung getrennt"
				+ exception != null ? ": " + exception : "");
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
				WorkerMain.getLogger().info("Empfangen: " + cmd);
				if (cmd.getAction() == COMPILE)
					CompileQueue.addJob(cmd);
				else if (cmd.getAction() == STARTAI)
				{
					try
					{
						SandboxCommand scmd = new SandboxCommand(RUN_AI,
								cmd.getAiId(), cmd.getVersion(), cmd.getLang(), cmd.getUuid(), cmd.getMaxRuntime());
						Sandbox s = Sandboxes.send(scmd);
						if (s == null)
						{
							WorkerMain.getLogger().todo("hier sollte evtl kein T-result geschickt werden");
							sendSandboxMessage(new SandboxMessage(TERMINATED_AI, cmd.getUuid()));
						}
						else
							Sandboxes.sandboxJobs.put(cmd.getUuid(), s);
					}
					catch (Exception e)
					{
						WorkerMain.getLogger().critical("Fehler beim Senden des StartKI-Befehls: " + e);
						Airbrake.log(e).printStackTrace();
					}
				}
				else if (cmd.getAction() == TERMAI || cmd.getAction() == KILLAI)
				{
					try
					{
						SandboxCommand scmd = new SandboxCommand(cmd.getAction() == TERMAI ? TERM_AI : KILL_AI,
								cmd.getAiId(), cmd.getVersion(), cmd.getLang(), cmd.getUuid(), cmd.getMaxRuntime());
						Sandbox s = Sandboxes.sandboxJobs.get(cmd.getUuid());
						if (s == null)
							WorkerMain.getLogger().critical("Das Backend hat mich beauftragt die unbekannte KI "
									+ cmd.getUuid() + " zu beenden.");
						else
							s.sendJob(scmd);
					}
					catch (IOException ioe)
					{
						WorkerMain.getLogger().critical("Fehler beim Senden des StopKI-Befehls: " + ioe);
						Airbrake.log(ioe).printStackTrace();
					}
				}
				else
					WorkerMain.getLogger().critical("Unbekannter Auftrag: " + cmd.getAction());
			}
			catch (Exception e)
			{
				WorkerMain.getLogger().critical("Failed to parse Command: " + e);
			}
		}
	}
	
	@Override
	public void packetSent (NIOSocket socket, Object tag)
	{
	}
}

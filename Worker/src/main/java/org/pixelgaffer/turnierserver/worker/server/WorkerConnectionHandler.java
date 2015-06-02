package org.pixelgaffer.turnierserver.worker.server;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.AI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.BACKEND;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.SANDBOX;

import java.io.IOException;

import lombok.Getter;
import naga.NIOSocket;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.messages.MessageForward;
import org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;
import org.pixelgaffer.turnierserver.worker.Sandbox;
import org.pixelgaffer.turnierserver.worker.Sandboxes;
import org.pixelgaffer.turnierserver.worker.WorkerMain;

public class WorkerConnectionHandler extends ConnectionHandler
{
	/** Der lokale Buffer mit den noch nicht gelesenen bytes. */
	private DataBuffer buffer = new DataBuffer();
	
	/** Informationen zum Typ dieser Connection. */
	@Getter
	private WorkerConnectionType type;
	
	/**
	 * Sollte es sich um eine Sandbox-Connection handeln, ist dies das
	 * zugehörige Sandbox-Objekt.
	 */
	private Sandbox sandbox;
	
	public WorkerConnectionHandler (NIOSocket socket)
	{
		super(socket);
	}
	
	/**
	 * Schickt den Job an den Client, sollte eine Sandbox sein.
	 */
	public void send (SandboxCommand job) throws IOException
	{
		if (type.getType() != SANDBOX)
			WorkerMain.getLogger().warning("WorkerConnectionHandler: Submitting StartAi Job to " + type.getType()
					+ "( should be " + SANDBOX + ")");
		getClient().write(Parsers.getSandbox().parse(job));
		getClient().write("\n".getBytes(UTF_8));
	}
	
	@Override
	public void disconnected ()
	{
		if (type != null)
		{
			switch (type.getType())
			{
				case AI:
					WorkerServer.aiConnections.remove(type.getUuid());
					break;
				case BACKEND:
					WorkerServer.backendConnection = null;
					break;
				case SANDBOX:
					Sandboxes.removeSandbox(sandbox);
					break;
			}
		}
	}
	
	@Override
	public void packetReceived (NIOSocket socket, byte[] packet)
	{
		buffer.add(packet);
		byte line[];
		while ((line = buffer.readLine()) != null)
		{
			// wenn type noch null ist, diesen lesen
			if (type == null)
			{
				String linestr = new String(line, UTF_8);
				type = WorkerConnectionType.parse(linestr);
				if (type == null)
				{
					WorkerMain.getLogger().severe(
							"WorkerConnectionHandler: Can't parse WorkerConnectionType from " + linestr);
					socket.close();
					return;
				}
				switch (type.getType())
				{
					case AI:
						WorkerServer.aiConnections.put(type.getUuid(), this);
						break;
					case BACKEND:
						WorkerServer.backendConnection = this;
						break;
					case SANDBOX:
						sandbox = new Sandbox(this);
						Sandboxes.addSandbox(sandbox);
						break;
				}
				WorkerMain.getLogger().info("WorkerConnectionHandler: Read type from " + socket.getIp() + ": " + type);
				continue;
			}
			
			switch (type.getType())
			{
				case AI:
					// von einer AI kommende Pakete zum Backend weiterleiten
					if ((WorkerServer.backendConnection != null) && WorkerServer.backendConnection.isConnected())
					{
						try
						{
							MessageForward mf = new MessageForward(type.getUuid(), line);
							DataBuffer buf = new DataBuffer();
							buf.add((byte)'M');
							buf.add(Parsers.getWorker().parse(mf));
							buf.add((byte)'\n');
							WorkerServer.backendConnection.getClient().write(buf.readAll());
						}
						catch (Exception e)
						{
							WorkerMain.getLogger().severe("WorkerConnectionHandler: Failed to forward: " + e);
						}
					}
					else
						WorkerMain.getLogger().severe("WorkerConnectionHandler: Connection to Backend not found.");
					break;
				
				case BACKEND:
					// vom Backend kommende Packete an die entsprechende KI
					// weiterleiten
					try
					{
						MessageForward mf = Parsers.getWorker().parse(line, MessageForward.class);
						System.out.println(mf);
					}
					catch (Exception e)
					{
						WorkerMain.getLogger().severe("WorkerConnectionHandler: Failed to forward: " + e);
					}
					break;
				
				case SANDBOX:
					try
					{
						SandboxMessage msg = Parsers.getSandbox().parse(line, SandboxMessage.class);
						System.out.println("WorkerConnectionHandler:133: " + msg);
					}
					catch (Exception e)
					{
						WorkerMain.getLogger().severe("WorkerConnectionHandler: Failed to parse message from Sandbox: " + e);
					}
					break;
			}
		}
	}
}

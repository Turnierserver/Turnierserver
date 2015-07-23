package org.pixelgaffer.turnierserver.worker.server;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.AI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.BACKEND;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.SANDBOX;

import java.io.IOException;

import lombok.Getter;
import lombok.ToString;
import naga.NIOSocket;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.bwprotocol.AiConnected;
import org.pixelgaffer.turnierserver.networking.messages.MessageForward;
import org.pixelgaffer.turnierserver.networking.messages.SandboxCommand;
import org.pixelgaffer.turnierserver.networking.messages.SandboxMessage;
import org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;
import org.pixelgaffer.turnierserver.worker.Sandbox;
import org.pixelgaffer.turnierserver.worker.Sandboxes;
import org.pixelgaffer.turnierserver.worker.WorkerMain;

@ToString(of = { "type" })
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
	public synchronized void sendJob (SandboxCommand job) throws IOException
	{
		if (type.getType() != SANDBOX)
			WorkerMain.getLogger().warning("Schicke Job an " + type.getType() + " (sollte " + SANDBOX + " sein)");
		getClient().write(Parsers.getSandbox().parse(job));
		getClient().write("\n".getBytes(UTF_8));
	}
	
	/**
	 * Schickt die Message an den Client, sollte eine KI sein.
	 */
	public synchronized void sendMessage (MessageForward mf)
	{
		if (type.getType() != AI)
			WorkerMain.getLogger().warning("Schicke Nachricht an " + type.getType() + " (sollte " + AI + " sein)");
		getClient().write(mf.getMessage());
		getClient().write("\n".getBytes(UTF_8));
	}
	
	@Override
	public void disconnected ()
	{
		if (type != null)
		{
			WorkerMain.getLogger().info(type + (type.getType() == SANDBOX ? " (" + sandbox + ")" : "")
					+ " hat die Verbindung getrennt.");
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
		else
			WorkerMain.getLogger().info(getClient().getIp() + " hat sich disconnected bevor er irgendwas gesendet hat");
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
					WorkerMain.getLogger().critical("Kann WorkerConnectionType nicht aus " + linestr + " lesen");
					socket.close();
					return;
				}
				switch (type.getType())
				{
					case AI:
						WorkerMain.getLogger().info("Die KI " + type.getUuid() + " hat sich verbunden");
						WorkerServer.aiConnections.put(type.getUuid(), this);
						try
						{
							WorkerMain.getBackendClient().sendAiConnected(new AiConnected(type.getUuid()));
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						break;
					case BACKEND:
						WorkerServer.backendConnection = this;
						WorkerMain.getLogger().info("Das Backend hat sich verbunden");
						break;
					case SANDBOX:
						sandbox = new Sandbox(this);
						sandbox.setLangs(type.getLangs());
						WorkerMain.getLogger().info("Eine neue Sandbox hat sich verbunden: " + sandbox);
						Sandboxes.addSandbox(sandbox);
						break;
				}
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
							buf.add(Parsers.getWorker().parse(mf));
							buf.add("\n".getBytes(UTF_8));
							WorkerServer.backendConnection.getClient().write(buf.readAll());
						}
						catch (Exception e)
						{
							WorkerMain.getLogger().critical("Fehler beim Weiterleiten: " + e);
						}
					}
					else
						WorkerMain.getLogger().critical("Habe keine Verbindung zum Backend gefunden");
					break;
				
				case BACKEND:
					// vom Backend kommende Packete an die entsprechende KI
					// weiterleiten
					try
					{
						MessageForward mf = Parsers.getWorker().parse(line, MessageForward.class);
						WorkerConnectionHandler con = WorkerServer.aiConnections.get(mf.getAi());
						if (con == null)
							throw new IllegalArgumentException("Unbekannte KI mit der UUID " + mf.getAi());
						con.sendMessage(mf);
					}
					catch (Exception e)
					{
						WorkerMain.getLogger().critical("Fehler beim Weiterleiten: " + e);
					}
					break;
				
				case SANDBOX:
					try
					{
						SandboxMessage msg = Parsers.getSandbox().parse(line, SandboxMessage.class);
						sandbox.sandboxAnswer(msg);
					}
					catch (Exception e)
					{
						WorkerMain.getLogger().critical("Fehler beim Lesen der Nachricht der Sandbox: " + e);
					}
					break;
			}
		}
	}
}

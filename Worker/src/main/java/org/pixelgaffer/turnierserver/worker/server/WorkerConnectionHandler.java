package org.pixelgaffer.turnierserver.worker.server;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.AI;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.BACKEND;
import static org.pixelgaffer.turnierserver.networking.messages.WorkerConnectionType.SANDBOX;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
		getClient().write(Parsers.getSandbox().parse(job, true));

	}
	
	/**
	 * Schickt die Message an den Client, sollte eine KI sein.
	 */
	public synchronized void sendMessage (MessageForward mf)
	{
		if (type.getType() != AI)
			WorkerMain.getLogger().warning("Schicke Nachricht an " + type.getType() + " (sollte " + AI + " sein)");
		WorkerMain.getLogger().debug("Starte Thread");
		new Thread(() -> {
			WorkerMain.getLogger().debug("Sende updateCpuTime request");
			try {
				Sandbox sandbox = Sandboxes.sandboxJobs.get(mf.getAi());
				WorkerMain.getLogger().debug(sandbox);
				sandbox.updateCpuTime();
			}
			catch(Exception e) {
				e.printStackTrace();
				return;
			}
			WorkerMain.getLogger().debug("Sende Nachricht");
			getClient().write(mf.getMessage());
			WorkerMain.getLogger().debug("Fertisch");
		}).start();
		WorkerMain.getLogger().debug("Thread gestartet");
		//getClient().write("\n".getBytes(UTF_8));
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
		WorkerMain.getLogger().debug("anfang");
		buffer.add(packet);
		byte line[];
		while ((line = buffer.readLine()) != null)
		{
			byte _line[] = line;
//			new Thread( () -> {
				WorkerMain.getLogger().debug("\t" + new String(_line, UTF_8));
				
				// wenn type noch null ist, diesen lesen
					if (type == null)
					{
						String linestr = new String(_line, UTF_8);
						type = WorkerConnectionType.parse(linestr);
						if (type == null)
						{
							WorkerMain.getLogger()
									.critical("Kann WorkerConnectionType nicht aus " + linestr + " lesen");
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
						return;
					}
					
					switch (type.getType())
					{
						case AI:
							// von einer AI kommende Pakete zum Backend
							// weiterleiten
							if ((WorkerServer.backendConnection != null)
									&& WorkerServer.backendConnection.isConnected())
							{
								try
								{
									MessageForward mf = new MessageForward(type.getUuid(), _line);
									DataBuffer buf = new DataBuffer();
									buf.add(Long.toString(Sandboxes.sandboxJobs.get(type.getUuid()).getCpuTimeDiff() / 1000000).getBytes(UTF_8));
									buf.add(Parsers.getWorker().parse(mf, true));
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
							// vom Backend kommende Packete an die entsprechende
							// KI
							// weiterleiten
							try
							{
								MessageForward mf = Parsers.getWorker().parse(_line, MessageForward.class);
								WorkerMain.getLogger().debug("Empfangen: " + mf);
								WorkerConnectionHandler con = WorkerServer.aiConnections.get(mf.getAi());
								if (con == null)
									throw new IllegalArgumentException("Unbekannte KI mit der UUID " + mf.getAi());
								WorkerMain.getLogger().debug("Sende:    " + mf);
								con.sendMessage(mf);
								WorkerMain.getLogger().debug("Gesendet: " + mf);
							}
							catch (Exception e)
							{
								WorkerMain.getLogger().critical("Fehler beim Weiterleiten: " + e);
							}
							break;
						
						case SANDBOX:
							try
							{
								SandboxMessage msg = Parsers.getSandbox().parse(_line, SandboxMessage.class);
								sandbox.sandboxAnswer(msg);
							}
							catch (Exception e)
							{
								WorkerMain.getLogger().critical("Fehler beim Lesen der Nachricht der Sandbox: " + e);
							}
							break;
					}
//				}).start();
		}
		WorkerMain.getLogger().debug("ende");
	}
}

package org.pixelgaffer.turnierserver.backend.server;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import lombok.NonNull;
import naga.NIOSocket;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.BackendMain;
import org.pixelgaffer.turnierserver.backend.Jobs;
import org.pixelgaffer.turnierserver.backend.WorkerConnection;
import org.pixelgaffer.turnierserver.backend.Workers;
import org.pixelgaffer.turnierserver.networking.ConnectionHandler;
import org.pixelgaffer.turnierserver.networking.messages.MessageForward;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommandAnswer;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;
import org.pixelgaffer.turnierserver.networking.util.DataBuffer;

/**
 * Diese Klasse ist der ConnectionHandler für den BackendServer.
 */
public class BackendWorkerConnectionHandler extends ConnectionHandler
{
	/** Der lokale Buffer mit den noch nicht gelesenen bytes. */
	private DataBuffer buffer = new DataBuffer();
	
	/** Die erstellte WorkerConnection. */
	private WorkerConnection workerConnection;
	
	
	public BackendWorkerConnectionHandler (NIOSocket socket)
	{
		super(socket);
	}
	
	public void sendCommand (@NonNull WorkerCommand cmd) throws IOException
	{
		getClient().write(Parsers.getWorker().parse(cmd));
		getClient().write("\n".getBytes(UTF_8));
	}
	
	public void sendMessage (MessageForward mf) throws IOException
	{
		workerConnection.sendMessage(mf);
	}
	
	@Override
	protected void disconnected ()
	{
		if (workerConnection != null)
		{
			Workers.removeWorker(workerConnection);
			workerConnection.disconnectClient();
		}
	}
	
	@Override
	public void packetReceived (NIOSocket socket, byte[] packet)
	{
		buffer.add(packet);
		byte line[];
		while ((line = buffer.readLine()) != null)
		{
			if (workerConnection == null)
			{
				try
				{
					workerConnection = new WorkerConnection(this, socket.getIp(),
							Parsers.getWorker().parse(line, WorkerInfo.class));
					Workers.registerWorker(workerConnection);
				}
				catch (Exception e)
				{
					BackendMain.getLogger().severe("BackendConnectionHandler: while creating WorkerConnection: " + e);
				}
			}
			else
			{
				try
				{
					// WorkerCommandSuccess success =
					// Parsers.getWorker().parse(line,
					// WorkerCommandSuccess.class);
					// Jobs.jobFinished(success);
					
					WorkerCommandAnswer answer = Parsers.getWorker().parse(line, WorkerCommandAnswer.class);
					if (answer.getWhat() == WorkerCommandAnswer.MESSAGE)
					{
						BackendFrontendCompileMessage msg = new BackendFrontendCompileMessage(answer.getMessage(), Jobs.findRequestId(answer.getUuid()));
						System.out.println(msg);
						BackendFrontendConnectionHandler.getFrontend().sendMessage(Parsers.getFrontend().parse(msg));
					}
					else if ((answer.getWhat() == WorkerCommandAnswer.CRASH)
							|| (answer.getWhat() == WorkerCommandAnswer.SUCCESS))
					{
						Jobs.jobFinished(answer);
					}
				}
				catch (Exception e)
				{
					BackendMain.getLogger().severe(
							"BackendWorkerConnectionHandler: Failed to parse answer from Worker: " + e);
				}
			}
		}
	}
}

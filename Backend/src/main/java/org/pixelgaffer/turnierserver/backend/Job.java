package org.pixelgaffer.turnierserver.backend;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import org.pixelgaffer.turnierserver.backend.server.message.BackendFrontendCommand;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;

@AllArgsConstructor
@ToString
public class Job
{
	@Getter
	private WorkerCommand workerCommand;
	
	@Getter
	private BackendFrontendCommand frontendCommand;
	
	@Getter
	private WorkerConnection worker;
	
	public UUID getUuid ()
	{
		return getWorkerCommand().getUuid();
	}
	
	public int getRequestId ()
	{
		return getFrontendCommand().getRequestid();
	}
}

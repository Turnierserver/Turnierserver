package org.pixelgaffer.turnierserver.backend;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.server.BackendFrontendCommand;
import org.pixelgaffer.turnierserver.backend.server.BackendFrontendConnectionHandler;
import org.pixelgaffer.turnierserver.backend.server.BackendFrontentCompileResult;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommandSuccess;

/**
 * Diese Klasse speichert Informationen zu den aktuell ausgeführten Jobs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Jobs
{
	/** Die Liste aller Jobs. */
	private static final List<Job> jobs = new ArrayList<>();
	
	/** Die Map mit den UUIDs und den zugehörigen Jobs. */
	private static final Map<UUID, Job> jobUuids = new HashMap<>();
	
	/** Die Map mit den Request IDs und den zugehörigen Jobs. */
	private static final Map<Integer, Job> jobRequestIds = new HashMap<>();
	
	public static void addJob (@NonNull Job job)
	{
		jobs.add(job);
		jobUuids.put(job.getUuid(), job);
		jobRequestIds.put(job.getRequestId(), job);
	}
	
	public static void processJob (@NonNull BackendFrontendCommand cmd)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException,
			FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
	{
		if (cmd.getAction().equals("compile"))
		{
			WorkerCommand wcmd = Workers.getAvailableWorker().compile(cmd.getId(), cmd.getGametype());
			Job job = new Job(wcmd, cmd);
			addJob(job);
		}
		else if (cmd.getAction().equals("start"))
		{
			Games.startGame(cmd.getGametype(), cmd.getAis());
		}
		else
			BackendMain.getLogger().severe(
					"Unknown action from Frontend: " + cmd.getAction());
	}
	
	public static void jobFinished (@NonNull WorkerCommandSuccess success) throws IOException
	{
		UUID uuid = success.getUuid();
		Job job = jobUuids.get(uuid);
		if (job == null)
		{
			BackendMain.getLogger().severe("Couldn't find job with UUID " + uuid);
			return;
		}
		int requestId = job.getRequestId();
		BackendFrontentCompileResult result = new BackendFrontentCompileResult(requestId, success.isSuccess());
		BackendFrontendConnectionHandler.getFrontend().sendMessage(Parsers.getFrontend().parse(result));
		jobs.remove(job);
		jobUuids.remove(uuid);
		jobRequestIds.remove(requestId);
	}
}

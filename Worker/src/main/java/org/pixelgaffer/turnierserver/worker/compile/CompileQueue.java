package org.pixelgaffer.turnierserver.worker.compile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Deque;
import java.util.LinkedList;

import org.pixelgaffer.turnierserver.compile.CompileResult;
import org.pixelgaffer.turnierserver.compile.Compiler;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import org.pixelgaffer.turnierserver.networking.bwprotocol.WorkerCommandAnswer;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;
import org.pixelgaffer.turnierserver.worker.WorkerMain;

/**
 * Diese Queue sorgt dafür, dass nur ein Compile-Job zur selben Zeit ausgeführt
 * wird, damit der Server nicht zu langsam wird und die KIs in den Sandboxen
 * einigermaßen gleiche Geschwindigkeitsverhältnisse haben.
 */
public class CompileQueue implements Runnable
{
	private static CompileQueue instance = new CompileQueue();
	
	public static void addJob (WorkerCommand job)
	{
		if (job.getAction() != WorkerCommand.COMPILE)
			throw new IllegalArgumentException();
		synchronized (instance.queue)
		{
			instance.queue.addLast(job);
			instance.queue.notifyAll();
		}
	}
	
	private final Deque<WorkerCommand> queue = new LinkedList<>();
	
	private CompileQueue ()
	{
		new Thread(this, "CompileQueue").start();
	}
	
	@Override
	public void run ()
	{
		while (true)
		{
			WorkerCommand job = null;
			
			synchronized (queue)
			{
				// wenn die queue leer ist, warten bis wieder was drinnen ist
				while (queue.isEmpty())
				{
					try
					{
						queue.wait();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				// den nächsten job holen
				job = queue.pollFirst();
			}
			
			// wenn der job nicht null ist, den job ausführen
			if (job != null)
			{
				WorkerMain.getLogger().info("Starte Kompiliervorgang: " + job);
				try
				{
					String lang = DatastoreFtpClient.retrieveAiLanguage(job.getAiId());
					Compiler c = Compiler.getCompiler(job.getAiId(), job.getVersion(), job.getGame(), lang);
					c.setUuid(job.getUuid());
					CompileResult result = c.compileAndUpload(WorkerMain.getBackendClient());
					DatastoreFtpClient.storeAiCompileOutput(job.getAiId(), job.getVersion(), result.getOutput());
					WorkerMain.getBackendClient().sendAnswer(
							new WorkerCommandAnswer(job.getAction(),
									result.isSuccessfull() ? WorkerCommandAnswer.SUCCESS : WorkerCommandAnswer.CRASH,
									job.getUuid(), null));
					result.getOutput().delete();
				}
				catch (Exception e)
				{
					WorkerMain.getLogger().critical("Fehler beim Kompilieren der KI " + job.getAiId());
					e.printStackTrace();
					
					try
					{
						File tmp = Files.createTempFile("exception", ".txt").toFile();
						PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tmp)));
						out.println("Während des Kompilierungsvorgangs ist ein interner Fehler aufgetreten.");
						e.printStackTrace(out);
						out.close();
						DatastoreFtpClient.storeAiCompileOutput(job.getAiId(), job.getVersion(), tmp);
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						WorkerMain.getBackendClient().sendAnswer(new WorkerCommandAnswer(job.getAction(),
								WorkerCommandAnswer.MESSAGE, job.getUuid(), sw.toString()));
						WorkerMain.getBackendClient().sendAnswer(new WorkerCommandAnswer(job.getAction(),
								WorkerCommandAnswer.CRASH, job.getUuid(), null));
						tmp.delete();
					}
					catch (Exception e1)
					{
						e1.printStackTrace();
					}
				}
				WorkerMain.getLogger().info("Kompilierauftrag fertig: " + job);
			}
		}
	}
}

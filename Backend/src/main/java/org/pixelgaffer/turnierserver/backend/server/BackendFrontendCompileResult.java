package org.pixelgaffer.turnierserver.backend.server;

import java.io.PrintWriter;
import java.io.StringWriter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BackendFrontendCompileResult
{
	/** Die request ID des Frontends. */
	@Getter
	public int requestid;
	
	/** Gibt an ob das Kompilieren erfolgreich war. */
	@Getter
	public boolean success;
	
	/** Die Exception, die beim Bearbeiten aufgetreten ist, oder null. */
	@Getter
	private String exception;
	
	public BackendFrontendCompileResult (int requestid, boolean success)
	{
		this.requestid = requestid;
		this.success = success;
	}
	
	public BackendFrontendCompileResult (int requestid, boolean success, Exception exception)
	{
		this.requestid = requestid;
		this.success = success;
		if (exception != null)
		{
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			this.exception = sw.toString();
		}
	}
}

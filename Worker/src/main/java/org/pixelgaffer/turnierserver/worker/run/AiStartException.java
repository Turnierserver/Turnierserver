package org.pixelgaffer.turnierserver.worker.run;

public class AiStartException extends Exception
{
	private static final long serialVersionUID = -8301146032554062368L;
	
	public AiStartException (int aiId, int aiVersion, String reason)
	{
		super(aiId + "v" + aiVersion + ": " + reason);
	}
}

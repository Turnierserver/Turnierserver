package org.pixelgaffer.turnierserver.compile;

import java.io.IOException;

import org.pixelgaffer.turnierserver.networking.messages.WorkerCommandAnswer;

public interface Backend
{
	public void sendAnswer (WorkerCommandAnswer answer) throws IOException;
}

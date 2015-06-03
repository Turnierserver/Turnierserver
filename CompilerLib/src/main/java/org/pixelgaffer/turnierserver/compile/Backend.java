package org.pixelgaffer.turnierserver.compile;

import java.io.IOException;

import org.pixelgaffer.turnierserver.networking.bwprotocol.WorkerCommandAnswer;

public interface Backend
{
	public void sendAnswer (WorkerCommandAnswer answer) throws IOException;
}

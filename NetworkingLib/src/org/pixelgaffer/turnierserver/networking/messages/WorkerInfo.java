package org.pixelgaffer.turnierserver.networking.messages;

import lombok.Getter;

public class WorkerInfo
{
	@Getter
	private int sandboxes;
	
	@Getter
	private int port;
}

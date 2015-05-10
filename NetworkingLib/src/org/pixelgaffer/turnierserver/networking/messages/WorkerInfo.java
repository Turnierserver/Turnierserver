package org.pixelgaffer.turnierserver.networking.messages;

import lombok.Getter;

import org.msgpack.annotation.Message;

@Message
public class WorkerInfo
{
	@Getter
	private int sandboxes;
	
	@Getter
	private int port;
}

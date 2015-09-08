package org.pixelgaffer.turnierserver.networking.messages;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;

public class MessageForward
{
	@NonNull
	@Getter
	private UUID ai;
	
	private String message;
	
	public MessageForward (@NonNull UUID ai, byte message[])
	{
		this.ai = ai;
		this.message = new String(message, UTF_8);
	}
	
	public byte[] getMessage ()
	{
		return message.getBytes(UTF_8);
	}
	
	@Override
	public String toString ()
	{
		return "MessageForward[ai=" + ai + ", message=" + message + "]";
	}
}

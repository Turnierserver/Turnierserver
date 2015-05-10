package org.pixelgaffer.turnierserver.networking.messages;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import org.msgpack.annotation.Message;
import org.msgpack.annotation.NotNullable;

@AllArgsConstructor
@Message
public class MessageForward
{
	@NonNull
	@NotNullable
	@Getter
	private UUID ai;
	
	@Getter
	private byte message[];
}

package org.pixelgaffer.turnierserver.networking.messages;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public class MessageForward
{
	@NonNull
	@Getter
	private UUID ai;
	
	@Getter
	private byte message[];
}

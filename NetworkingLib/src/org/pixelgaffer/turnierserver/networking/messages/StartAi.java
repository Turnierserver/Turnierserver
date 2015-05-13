package org.pixelgaffer.turnierserver.networking.messages;

import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

@ToString
public class StartAi
{
	@Getter
	private String user, name;
	
	@Getter
	private int version;
	
	@Getter
	private UUID uuid;
}

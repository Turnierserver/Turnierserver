package org.pixelgaffer.turnierserver.networking.bwprotocol;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class AiConnected
{
	@Getter
	private UUID uuid;
}

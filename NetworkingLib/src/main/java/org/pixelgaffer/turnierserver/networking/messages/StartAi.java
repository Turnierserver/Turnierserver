package org.pixelgaffer.turnierserver.networking.messages;

import java.util.UUID;

import lombok.Getter;
import lombok.ToString;

/**
 * Diese Klasse dient dazu, die KI, die die Sandbox starten soll, zu beschreiben.
 */
@ToString
public class StartAi
{
	/** Die id der KI. */
	@Getter
	private int id;
	
	/** Die Version der KI. */
	@Getter
	private int version;
	
	/** Die UUID die das Backend der KI zugewiesen hat. */
	@Getter
	private UUID uuid;
}

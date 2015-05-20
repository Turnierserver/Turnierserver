package org.pixelgaffer.turnierserver.worker.server;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Diese Klasse dient dazu, der Sandbox befehle zu erteilen.
 */
@AllArgsConstructor
@ToString
public class SandboxCommand
{
	public static final char RUN_AI = 'R';
	public static final char KILL_AI = 'K';
	
	/** Der Befehl. */
	@Getter
	private char command;
	
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

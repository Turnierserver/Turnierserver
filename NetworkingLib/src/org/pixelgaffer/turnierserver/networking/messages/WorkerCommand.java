package org.pixelgaffer.turnierserver.networking.messages;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Dieser Command wird vom Backend über den auf dem Backend laufenden Server an
 * den Worker geschickt.
 */
@AllArgsConstructor
@ToString
public class WorkerCommand
{
	public static final char COMPILE = 'C';
	public static final char STARTAI = 'S';
	
	/** Die Aktion, die der Worker ausführen soll. */
	@Getter
	private char action;
	
	/** Die ID der KI. */
	@Getter
	private int aiId;
	
	/** Die Version der KI. */
	@Getter
	private int version;
	
	/** Die UUID die das Backend diesem Befehl zugewiesen hat. */
	@Getter
	private UUID uuid;
}

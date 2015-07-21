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
	public static final char TERMAI = 'T';
	public static final char KILLAI = 'K';
	
	/** Die Aktion, die der Worker ausführen soll. */
	@Getter
	private char action;
	
	/** Die ID der KI. */
	@Getter
	private int aiId;
	
	/** Die Version der KI. */
	@Getter
	private int version;
	
	/** Die Sprache der KI. */
	@Getter
	private String lang;
	
	/** Das zugehörige Spiel. */
	@Getter
	private int game;
	
	/** Die UUID die das Backend diesem Job zugewiesen hat. */
	@Getter
	private UUID uuid;
}

package org.pixelgaffer.turnierserver.networking.messages;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class WorkerCommandAnswer
{
	public static final char CRASH = 'C';
	public static final char MESSAGE = 'M';
	public static final char SUCCESS = 'S';
	
	/** Die Aktion, die der Worker ausgeführt hat. */
	@Getter
	private char action;
	
	/** Gibt an, was gemeldet wird. */
	@Getter
	private char what;
	
	/** Die UUID die das Backend diesem Job zugewiesen hat. */
	@Getter
	private UUID uuid;
	
	/** Die Message die an das Backend geschickt werden soll. */
	@Getter
	private String message;
}

package org.pixelgaffer.turnierserver.networking.messages;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Dieser Command wird vom Worker über den auf dem Backend laufenden Server ans
 * Backend als Nachricht über den Erfolg eines Jobs geschickt.
 */
@AllArgsConstructor
@ToString
public class WorkerCommandSuccess
{
	/** Die Aktion, die der Worker ausgeführt hat. */
	@Getter
	private char action;
	
	/** Gibt an, ob der Job erfolgreich beendet wurde oder nicht. */
	@Getter
	private boolean success;
	
	/** Die UUID die das Backend diesem Job zugewiesen hat. */
	@Getter
	private UUID uuid;
}

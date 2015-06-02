package org.pixelgaffer.turnierserver.worker.server;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Diese Message wird von der Sandbox geschickt, wenn sich der Zustand einer KI
 * ändert.
 */
@AllArgsConstructor
@ToString
public class SandboxMessage
{
	/** KI gestartet. */
	public static final char STARTED_AI = 'S';
	/** KI hat sich selbst beendet. */
	public static final char FINISHED_AI = 'F';
	/** KI auf Auftrag des Backends/Workers/SIGKILL beendet. */
	public static final char TERMINATED_AI = 'T';
	/** KI auf Auftrag der Logik beendet. */
	public static final char KILLED_AI = 'K';
	
	/** Die Zustandsänderung der KI. */
	@Getter
	private char event;
	
	/** Die vom Backend zugewiesene UUID. */
	@Getter
	private UUID uuid;
}

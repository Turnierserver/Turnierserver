package org.pixelgaffer.turnierserver.networking.messages;

import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Diese Message wird von der Sandbox geschickt, wenn sich der Zustand einer KI
 * ändert.
 */
@RequiredArgsConstructor
@ToString(exclude={"cpuTime"})
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
	public static final char CPU_TIME = 'C';
	
	/** Die Zustandsänderung der KI. */
	@Getter @NonNull
	private char event;
	
	/** Die vom Backend zugewiesene UUID. */
	@Getter @NonNull
	private UUID uuid;
	
	@Getter
	private long cpuTime = -1;
}

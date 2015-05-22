package org.pixelgaffer.turnierserver.worker.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Ein Request der Sandbox, das Binary-Archiv der angegebenen KI zurückzuschicken.
 */
@AllArgsConstructor
@ToString
public class MirrorRequest
{
	/** Die ID der KI. */
	@Getter
	private int id;
	
	/** Die Version der KI. */
	@Getter
	private int version;
}

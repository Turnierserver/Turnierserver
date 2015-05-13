package org.pixelgaffer.turnierserver.backend.server;

import lombok.Getter;
import lombok.ToString;

import org.msgpack.annotation.Message;
import org.msgpack.annotation.Optional;

/**
 * Diese Message parst einen Befehl vom Frontend.
 */
@Message
@ToString
public class BackendFrontendCommand
{
	/** Der Befehl des Frontends. */
	@Getter
	private String action;
	
	/** Die request-id des Frontends. */
	@Getter
	private int requestid;
	
	// action=compile
	
	/** Falls der Befehl kompilieren ist, ist dies die id der KI. */
	@Getter
	@Optional
	private String id;
	
	// action=start
	
	/** Falls der Befehl Spiel starten ist, ist dies die id des Spiels. */
	@Getter
	@Optional
	private int gametype;
	
	/** Falls der Befehl Spiel starten ist, sind dies die beteiligten KIs. */
	@Getter
	@Optional
	private String ais[];
}

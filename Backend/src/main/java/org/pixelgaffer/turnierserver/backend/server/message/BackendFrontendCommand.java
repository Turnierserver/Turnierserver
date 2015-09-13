/*
 * BackendFrontendCommand.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pixelgaffer.turnierserver.backend.server.message;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Diese Message parst einen Befehl vom Frontend.
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class BackendFrontendCommand
{
	/** Der Befehl des Frontends. */
	@Getter
	private String action;
	
	/** Die request-id des Frontends. */
	@Getter
	private int requestid;
	
	/** Die id des Spiels. */
	@Getter
	private int gametype;
	
	// action=compile oder action=qualify
	
	/** Falls der Befehl kompilieren ist, ist dies die id der KI. */
	@Getter
	private String id;
	
	// action=start
	
	/** Falls der Befehl Spiel starten ist, sind dies die beteiligten KIs. */
	@Getter
	private String ais[];
	
	/** Die Sprache der Ki, beim ausführen */
	@Getter
	private String languages[];
	
	/** Die Sprache der Ki, beim kompilieren*/
	@Getter
	private String language;
}

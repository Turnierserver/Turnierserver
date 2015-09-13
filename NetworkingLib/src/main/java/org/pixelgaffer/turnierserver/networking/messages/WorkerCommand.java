/*
 * WorkerCommand.java
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
	
	/** Die maximale Laufzeit der KI. Wird von isolate begrenzt. */
	@Getter
	private float maxRuntime;
}

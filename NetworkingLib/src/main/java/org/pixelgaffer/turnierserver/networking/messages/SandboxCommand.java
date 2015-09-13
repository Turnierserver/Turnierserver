/*
 * SandboxCommand.java
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
 * Diese Klasse dient dazu, der Sandbox befehle zu erteilen.
 */
@AllArgsConstructor
@ToString
public class SandboxCommand
{
	public static final char RUN_AI = 'R';
	public static final char TERM_AI = 'T';
	public static final char KILL_AI = 'K';
	public static final char CPU_TIME = 'C';
	
	/** Der Befehl. */
	@Getter
	private char command;
	
	/** Die id der KI. */
	@Getter
	private int id;
	
	/** Die Version der KI. */
	@Getter
	private int version;
	
	/** Die Sprache der KI. */
	@Getter
	private String lang;
	
	/** Die UUID die das Backend der KI zugewiesen hat. */
	@Getter
	private UUID uuid;
	
	/** Die maximale Laufzeit der KI. Wird von isolate begrenzt. */
	@Getter
	private float maxRuntime;
}

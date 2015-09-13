/*
 * WorkerCommandAnswer.java
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
package org.pixelgaffer.turnierserver.networking.bwprotocol;

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

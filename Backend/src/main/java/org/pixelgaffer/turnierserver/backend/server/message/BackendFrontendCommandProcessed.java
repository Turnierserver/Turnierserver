/*
 * BackendFrontendCommandProcessed.java
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

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BackendFrontendCommandProcessed
{
	/**
	 * Die request ID des Frontends.
	 */
	@Getter
	public int requestid;
	
	/**
	 * Die 'Aktion' des Paketes (hier "processed")
	 */
	@Getter
	public String status = "processed";
	
	public BackendFrontendCommandProcessed (int requestid)
	{
		this.requestid = requestid;
	}
}

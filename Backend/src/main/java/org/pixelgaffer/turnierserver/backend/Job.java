/*
 * Job.java
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
package org.pixelgaffer.turnierserver.backend;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import org.pixelgaffer.turnierserver.backend.server.message.BackendFrontendCommand;
import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;

@AllArgsConstructor
@ToString
public class Job
{
	@Getter
	private WorkerCommand workerCommand;
	
	@Getter
	private BackendFrontendCommand frontendCommand;
	
	@Getter
	private WorkerConnection worker;
	
	public UUID getUuid ()
	{
		return getWorkerCommand().getUuid();
	}
	
	public int getRequestId ()
	{
		return getFrontendCommand().getRequestid();
	}
}

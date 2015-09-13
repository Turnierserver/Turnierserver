/*
 * WorkerConnectionPool.java
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
package org.pixelgaffer.turnierserver.worker.server;

import naga.NIOSocket;

import org.pixelgaffer.turnierserver.networking.ConnectionPool;

public class WorkerConnectionPool extends ConnectionPool<WorkerConnectionHandler>
{
	public boolean add (NIOSocket socket)
	{
		return add(new WorkerConnectionHandler(socket));
	}
}

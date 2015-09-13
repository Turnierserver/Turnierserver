/*
 * ConnectionPool.java
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
package org.pixelgaffer.turnierserver.networking;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class ConnectionPool <H extends ConnectionHandler>
{
	@Getter
	@Setter
	private int maxConnections = -1;
	
	public ConnectionPool (int maxConnections)
	{
		setMaxConnections(maxConnections);
	}
	
	private List<H> connections = new ArrayList<>();
	
	public boolean add (H handler)
	{
		handler.setPool(this);
		if ((handler == null) || ((maxConnections > 0) && (connections.size() > maxConnections)))
		{
			if (handler != null)
				handler.disconnect();
			return false;
		}
		return connections.add(handler);
	}
	
	public boolean remove (ConnectionHandler handler)
	{
		return connections.remove(handler);
	}
}

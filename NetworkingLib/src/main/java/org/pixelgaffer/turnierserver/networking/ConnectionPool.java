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

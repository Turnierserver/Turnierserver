/*
 * NetworkService.java
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

import java.io.IOException;

import naga.NIOService;

public class NetworkService
{
	private static NIOService service;
	
	public static NIOService getService () throws IOException
	{
		if (service == null)
		{
			service = new NIOService();
			service.setBufferSize(1*1024*1024); // 1 MiB
		}
		return service;
	}
	
	public static void mainLoop ()
	{
		try
		{
			while (true)
			{
				service.selectBlocking();
			}
		}
		catch (Exception e)
		{
		}
	}
}

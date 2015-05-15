package org.pixelgaffer.turnierserver.networking;

import java.io.IOException;

import naga.NIOService;

public class NetworkService
{
	private static NIOService service;
	
	public static NIOService getService () throws IOException
	{
		if (service == null)
			service = new NIOService();
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

package org.pixelgaffer.turnierserver.backend;

import java.io.IOException;
import java.util.logging.Logger;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import naga.ConnectionAcceptor;

import org.pixelgaffer.turnierserver.PropertiesLoader;
import org.pixelgaffer.turnierserver.backend.server.BackendFrontendServer;
import org.pixelgaffer.turnierserver.backend.server.BackendWorkerServer;
import org.pixelgaffer.turnierserver.networking.NetworkService;

/**
 * Diese Klasse startet das Backend und enthält einige für das ganze Programm
 * nützliche Funktionen.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BackendMain
{
	/** Gibt den Standartlogger zurück. */
	public static Logger getLogger ()
	{
		return Logger.getLogger("BackendServer");
	}
	
	/** Gibt eine Integer-Property oder den Defaultwert zurück. */
	static int getIntProp (String name, int defaultValue)
	{
		String value = System.getProperty(name);
		if (value == null)
			return defaultValue;
		return Integer.valueOf(value);
	}
	
	public static void main (String args[]) throws IOException
	{
		// Properties laden
		PropertiesLoader.loadProperties(args.length > 0 ? args[0] : "/etc/turnierserver/turnierserver.prop");
		
		// Server starten
		getLogger().info("BackendServer starting");
		int port = getIntProp("turnierserver.backend.workerserver.port", BackendWorkerServer.DEFAULT_PORT);
		int maxClients = getIntProp("turnierserver.backend.workerserver.maxClients", -1);
		BackendWorkerServer server0 = new BackendWorkerServer(port, maxClients);
		port = getIntProp("turnierserver.backend.frontendserver.port", BackendFrontendServer.DEFAULT_PORT);
		BackendFrontendServer server1 = new BackendFrontendServer(port, 1);
		server0.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		server1.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		new Thread( () -> NetworkService.mainLoop(), "NetworkService").start();
		getLogger().info("BackendServer started");
	}
}

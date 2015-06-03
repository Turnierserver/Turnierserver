package org.pixelgaffer.turnierserver.worker;

import java.io.IOException;
import java.util.logging.Logger;

import lombok.Getter;
import naga.ConnectionAcceptor;

import org.pixelgaffer.turnierserver.PropertyUtils;
import org.pixelgaffer.turnierserver.networking.NetworkService;
import org.pixelgaffer.turnierserver.networking.messages.WorkerInfo;
import org.pixelgaffer.turnierserver.worker.backendclient.BackendClient;
import org.pixelgaffer.turnierserver.worker.server.MirrorServer;
import org.pixelgaffer.turnierserver.worker.server.WorkerServer;

public class WorkerMain
{
	public static final WorkerInfo workerInfo = new WorkerInfo();
	
	public static void notifyInfoUpdated () throws IOException
	{
		getBackendClient().sendInfo(workerInfo);
	}
	
	@Getter
	private static BackendClient backendClient;
	
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
		PropertyUtils.loadProperties(args.length > 0 ? args[0] : "/etc/turnierserver/turnierserver.prop");
		
		// Server starten
		getLogger().info("BackendServer starting");
		int port = getIntProp("turnierserver.worker.server.port", WorkerServer.DEFAULT_PORT);
		workerInfo.setPort(port);
		int maxClients = getIntProp("turnierserver.worker.server.maxClients", -1);
		WorkerServer server = new WorkerServer(port, maxClients);
		server.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		new Thread( () -> NetworkService.mainLoop(), "NetworkService").start();
		getLogger().info("BackendServer started");
		
		// Connect to Backend
		backendClient = new BackendClient(PropertyUtils.getStringRequired(PropertyUtils.BACKEND_HOST),
				PropertyUtils.getIntRequired(PropertyUtils.BACKEND_WORKER_SERVER_PORT));
		
		// Mirror starten
		port = PropertyUtils.getInt("turnierserver.worker.mirror.port", MirrorServer.DEFAULT_PORT);
		maxClients = PropertyUtils.getInt("turnierserver.worker.mirror.maxClients", -1);
		MirrorServer mirror = new MirrorServer(port, maxClients);
		mirror.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		getLogger().info("MirrorServer started");
	}
}

/*
 * WorkerMain.java
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
package org.pixelgaffer.turnierserver.worker;

import static org.pixelgaffer.turnierserver.PropertyUtils.BACKEND_HOST;
import static org.pixelgaffer.turnierserver.PropertyUtils.BACKEND_WORKER_SERVER_PORT;
import static org.pixelgaffer.turnierserver.PropertyUtils.WORKER_MIRROR_PORT;
import static org.pixelgaffer.turnierserver.PropertyUtils.WORKER_SERVER_MAX_CLIENTS;
import static org.pixelgaffer.turnierserver.PropertyUtils.WORKER_SERVER_PORT;
import static org.pixelgaffer.turnierserver.PropertyUtils.getInt;
import static org.pixelgaffer.turnierserver.PropertyUtils.getIntRequired;
import static org.pixelgaffer.turnierserver.PropertyUtils.getStringRequired;
import static org.pixelgaffer.turnierserver.PropertyUtils.loadProperties;

import java.io.IOException;

import lombok.Getter;
import naga.ConnectionAcceptor;

import org.pixelgaffer.turnierserver.Logger;
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
		if (getBackendClient().isConnected())
			getBackendClient().sendInfo(workerInfo);
	}
	
	@Getter
	private static BackendClient backendClient;
	
	@Getter
	private static final Logger logger = new Logger();
	
	public static void main (String args[]) throws IOException
	{
		// Properties laden
		loadProperties(args.length > 0 ? args[0] : "/etc/turnierserver/turnierserver.prop");
		
		// Server starten
		getLogger().info("WorkerServer starting");
		int port = getInt(WORKER_SERVER_PORT, WorkerServer.DEFAULT_PORT);
		workerInfo.setPort(port);
		int maxClients = getInt(WORKER_SERVER_MAX_CLIENTS, -1);
		WorkerServer server = new WorkerServer(port, maxClients);
		server.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		new Thread( () -> NetworkService.mainLoop(), "NetworkService").start();
		getLogger().info("WorkerServer started");
		
		// Connect to Backend
		backendClient = new BackendClient(getStringRequired(BACKEND_HOST), getIntRequired(BACKEND_WORKER_SERVER_PORT));
		
		// Mirror starten
		port = getInt(WORKER_MIRROR_PORT, MirrorServer.DEFAULT_PORT);
		MirrorServer mirror = new MirrorServer(port);
		mirror.start();
		getLogger().info("MirrorServer started");
	}
}

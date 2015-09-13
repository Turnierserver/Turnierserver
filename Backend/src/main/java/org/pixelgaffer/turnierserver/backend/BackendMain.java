/*
 * BackendMain.java
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

import static org.pixelgaffer.turnierserver.PropertyUtils.BACKEND_FRONTEND_SERVER_PORT;
import static org.pixelgaffer.turnierserver.PropertyUtils.BACKEND_WORKER_SERVER_MAX_CLIENTS;
import static org.pixelgaffer.turnierserver.PropertyUtils.BACKEND_WORKER_SERVER_PORT;
import static org.pixelgaffer.turnierserver.PropertyUtils.getInt;
import static org.pixelgaffer.turnierserver.PropertyUtils.loadProperties;

import java.io.File;
import java.io.IOException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import naga.ConnectionAcceptor;

import org.pixelgaffer.turnierserver.Airbrake;
import org.pixelgaffer.turnierserver.Logger;
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
	@Getter
	private static final Logger logger = new Logger();
	
	static final File jobsStore = new File("/var/spool/backend/jobs");
	
	public static void main (String args[]) throws IOException
	{
		// Properties laden
		loadProperties(args.length > 0 ? args[0] : "/etc/turnierserver/turnierserver.prop");
		
		// Zeugs restoren
		if (jobsStore.exists())
		{
			try
			{
				Jobs.restoreJobs(jobsStore);
			}
			catch (Exception e)
			{
				Airbrake.log(e).printStackTrace();
			}
			jobsStore.delete();
		}
		
		// Server starten
		int port = getInt(BACKEND_WORKER_SERVER_PORT, BackendWorkerServer.DEFAULT_PORT);
		int maxClients = getInt(BACKEND_WORKER_SERVER_MAX_CLIENTS, -1);
		BackendWorkerServer server0 = new BackendWorkerServer(port, maxClients);
		port = getInt(BACKEND_FRONTEND_SERVER_PORT, BackendFrontendServer.DEFAULT_PORT);
		BackendFrontendServer server1 = new BackendFrontendServer(port);
		server0.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		server1.setConnectionAcceptor(ConnectionAcceptor.ALLOW);
		new Thread( () -> NetworkService.mainLoop(), "NetworkService").start();
		getLogger().info("Backend started");
		
		// eine ShutdownHook zum Speichern erstellen
		Runtime.getRuntime().addShutdownHook(new Thread( () -> {
			
			getLogger().info("Received shutdown signal");
			Workers.shutdown();
			try
			{
				Jobs.storeJobs(jobsStore);
			}
			catch (Exception e)
			{
				Airbrake.log(e).printStackTrace();
			}
			
		}, "BackendMain-ShutdownHook"));
	}
}

package org.pixelgaffer.turnierserver.backend.server;

import java.net.Socket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectionHandler
{
	@NonNull
	private Socket client;
	
	
}

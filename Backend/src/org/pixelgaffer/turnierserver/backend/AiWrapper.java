package org.pixelgaffer.turnierserver.backend;

import org.pixelgaffer.turnierserver.backend.server.ConnectionHandler;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class AiWrapper
{
	@NonNull @Getter
	private Game game;
	
	@Setter @Getter
	private int id;
	
	@Setter @Getter
	private ConnectionHandler connectionHandler;
	
	public void receiveMessage (String message)
	{
		getGame().getLogic().receiveMessage(message, getId());
	}
	
	public void sendMessage (String message)
	{
		throw new UnsupportedOperationException();
	}
}

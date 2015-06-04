package org.pixelgaffer.turnierserver.codr.simulator;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;

public class CodrAiWrapper implements Ai
{
	/** Das zugrundeliegende Spiel, enthält die Spiellogik. */
	@NonNull
	@Getter
	private CodrGameImpl game;
	
	/** Die UUID der KI im Netzwerk. */
	@Getter
	@NonNull
	private UUID uuid;
	
	/** Gibt an ob sich die KI schon verbunden hat. */
	@Getter
	@Setter
	private boolean connected = false;
	
	/** Der ID-String dieser KI. */
	@Setter
	@Getter
	private String id;
	
	/** Der Index der KI in der Liste. */
	@Getter
	@Setter
	private int index;
	
	/** Hier kann die Spiellogik Informationen über die KI abspeichern. */
	@Getter
	@Setter
	private AiObject object;
	
	@Override
	public void sendMessage (byte[] message) throws IOException
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void disconnect () throws IOException
	{
		throw new UnsupportedOperationException();
	}
}

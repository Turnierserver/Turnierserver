package org.pixelgaffer.turnierserver.backend;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.pixelgaffer.turnierserver.backend.Games.GameImpl;
import org.pixelgaffer.turnierserver.backend.server.BackendWorkerConnectionHandler;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;
import org.pixelgaffer.turnierserver.networking.messages.MessageForward;

/**
 * Diese Klasse repräsentiert eine KI intern für Backend und Spiellogik.
 */
@RequiredArgsConstructor
public class AiWrapper implements Ai
{
	/** Das zugrundeliegende Spiel, enthält die Spiellogik. */
	@NonNull
	@Getter
	private GameImpl game;
	
	/** Die UUID der KI im Netzwerk. */
	@Getter
	@NonNull
	private UUID uuid;
	
	/** Die ID dieser KI. */
	@Setter
	@Getter
	private int id;
	
	/** Die Version dieser KI. */
	@Setter
	@Getter
	private int version;
	
	/** Der Index der KI in der Liste. */
	@Getter
	@Setter
	private int index;
	
	/** Der {@link BackendWorkerConnectionHandler} dieser KI. */
	@Setter
	@Getter
	private BackendWorkerConnectionHandler connectionHandler;
	
	/** Empfängt eine Nachricht und leitet sie an die Speillogik weiter. */
	public void receiveMessage (byte message[])
	{
		getGame().getLogic().receiveMessage(message, this);
	}
	
	/** Sendet eine Nachricht an die KI. */
	public void sendMessage (byte message[]) throws IOException
	{
		if (connectionHandler == null)
			throw new IOException("Not connected");
		MessageForward mf = new MessageForward(uuid, message);
		connectionHandler.sendMessage(mf);
	}
	
	/** Hier kann die Spiellogik Informationen über die KI abspeichern. */
	@Getter
	@Setter
	private AiObject object;
	
	@Override
	public void disconnect ()
	{
		throw new UnsupportedOperationException();
	}
}

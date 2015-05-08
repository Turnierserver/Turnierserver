package org.pixelgaffer.turnierserver.backend;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.pixelgaffer.turnierserver.backend.server.BackendConnectionHandler;

/**
 * Diese Klasse repräsentiert eine KI intern für Backend und Spiellogik.
 */
@RequiredArgsConstructor
public class AiWrapper
{
	/** Das zugrundeliegende Spiel, enthält die Spiellogik. */
	@NonNull @Getter
	private Game game;
	
	/** Die ID dieser KI. */
	@Setter @Getter
	private int id;
	
	/** Der {@link BackendConnectionHandler} dieser KI. */
	@Setter @Getter
	private BackendConnectionHandler connectionHandler;
	
	/** Empfängt eine Nachricht und leitet sie an die Speillogik weiter. */
	public void receiveMessage (String message)
	{
		getGame().getLogic().receiveMessage(message, this);
	}
	
	/** Sendet eine Nachricht an die KI. */
	public void sendMessage (String message)
	{
		throw new UnsupportedOperationException();
	}
	
	/** Hier kann die Spiellogik Informationen über die KI abspeichern. */
	@Getter @Setter
	private Object userObject;
}

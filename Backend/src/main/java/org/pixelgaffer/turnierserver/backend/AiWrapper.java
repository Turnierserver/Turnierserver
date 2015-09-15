/*
 * AiWrapper.java
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

import java.io.IOException;
import java.util.UUID;
import org.pixelgaffer.turnierserver.backend.Games.GameImpl;
import org.pixelgaffer.turnierserver.backend.Games.GameImpl.GameState;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;
import org.pixelgaffer.turnierserver.networking.messages.MessageForward;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
	@Setter(AccessLevel.PACKAGE)
	@NonNull
	private UUID uuid;
	
	/** Der ID-String dieser KI. */
	@Setter
	@Getter
	private String id;
	
	/** Die ID dieser KI. */
	@Setter
	@Getter
	private int aiId;
	
	/** Die Version dieser KI. */
	@Setter
	@Getter
	private int version;
	
	/** Der Index der KI in der Liste. */
	@Getter
	@Setter
	private int index;
	
	/** Die {@link WorkerConnection} dieser KI. */
	@Setter
	@Getter
	private WorkerConnection connection;
	
	/** Gibt an, ob die KI mit dem Worker verbunden ist. */
	@Getter
	private boolean connected;
	
	/** Die Programmiersprache dieser KI. */
	@Getter
	@NonNull
	private String lang;
	
	/**
	 * Wird aufgerufen wenn die KI mit dem Worker verbunden wurde. Wenn alle
	 * KIs verbunden sind, wird das Spiel endgültig gestartet.
	 */
	public void connected ()
	{
		BackendMain.getLogger().info("Die KI " + uuid + " hat sich verbunden!");
		connected = true;
		getGame().aiConnected();
	}
	
	/**
	 * Empfängt eine Nachricht und leitet sie an die Spiellogik weiter, wenn
	 * diese gestartet ist.
	 */
	public void receiveMessage (byte message[])
	{
		if (getGame().getLogic().isStarted())
			getGame().getLogic().receiveMessage(message, this);
	}
	
	/** Sendet eine Nachricht an die KI. */
	public void sendMessage (byte message[]) throws IOException
	{
		if (connection == null)
			throw new IOException("Not connected");
		MessageForward mf = new MessageForward(uuid, message);
		connection.sendMessage(mf);
	}
	
	/** Hier kann die Spiellogik Informationen über die KI abspeichern. */
	@Getter
	@Setter
	private AiObject object;
	
	@Override
	public void disconnect () throws IOException
	{
		BackendMain.getLogger().info("Die KI " + uuid + " wird disconnected");
		connected = false;
		connection.killJob(this);
	}
	
	/**
	 * Wird aufgerufen, wenn die KI, aus welchen Gründen auch immer,
	 * abgestürtzt ist oder sich beendet hat.
	 */
	public void crashed ()
	{
		BackendMain.getLogger().info("Die KI " + uuid + " ist abgestürtzt");
		
		// wenn das Spiel noch läuft der GameLogik mitteilen dass die KI sich
		// beendet hat
		if (getGame().getState() == GameState.STARTED)
			getGame().getLogic().aiCrashed(this);
			
		// wenn sich die KI noch gar nicht verbunden hat diese als gestartet
		// markieren damit
		// die GameLogik das spiel startet und die KI entsprechend behandelt
		if (!isConnected())
			connected();
	}
}

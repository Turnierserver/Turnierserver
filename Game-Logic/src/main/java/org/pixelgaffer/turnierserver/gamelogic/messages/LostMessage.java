package org.pixelgaffer.turnierserver.gamelogic.messages;

import java.util.UUID;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LostMessage {
	
	@NonNull
	public String reason;
	@NonNull
	public String id;
	/**
	 * Die requestid des Frontendauftrages
	 */
	@NonNull
	public int requestid;
	public boolean isCrash = true;
	/**
	 * Die ID des Spieles in einem Turnier
	 */
	@NonNull
	public UUID gameId;
}

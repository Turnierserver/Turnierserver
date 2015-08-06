package org.pixelgaffer.turnierserver.gamelogic.messages;

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
}

package org.pixelgaffer.turnierserver.backend.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BackendFrontendCommandProcessed {
	/**
	 * Die request ID des Frontends.
	 */
	@Getter
	public int requestid;

	/**
	 * Die 'Aktion' des Paketes (hier "processed")
	 */
	@Getter
	public String action = "processed";

	public BackendFrontendCommandProcessed(int requestid) {
		this.requestid = requestid;
	}
}

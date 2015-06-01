package org.pixelgaffer.turnierserver.backend.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Diese Klasse wird benutzt um dem Frontend Nachrichten vom Kompiliervorgang
 * auf dem Worker zu schicken.
 */
@AllArgsConstructor
public class BackendFrontendCompileMessage
{
	@Getter
	private String compilelog;
	
	@Getter
	private int requestid;
}

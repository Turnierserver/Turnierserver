package org.pixelgaffer.turnierserver.backend.server.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Diese Klasse wird benutzt um dem Frontend Nachrichten vom Kompiliervorgang
 * auf dem Worker zu schicken.
 */
@AllArgsConstructor
@ToString
public class BackendFrontendCompileMessage
{
	@Getter
	private String compilelog;
	
	@Getter
	private int requestid;
}

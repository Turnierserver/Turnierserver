package org.pixelgaffer.turnierserver.backend.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BackendFrontentCompileResult
{
	/** Die request ID des Frontends. */
	@Getter
	public int requestid;
	
	/** Gibt an ob das Kompilieren erfolgreich war. */
	@Getter
	public boolean success;
}

package org.pixelgaffer.turnierserver.compile;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CompileResult
{
	@Getter
	private boolean successfull;
	
	@Getter
	private File output;
}

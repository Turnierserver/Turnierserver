package org.pixelgaffer.katepartparser.context;

import lombok.ToString;

@ToString(callSuper = true)
public class DetectIdentifier extends RegExpr
{
	public DetectIdentifier ()
	{
		String = "[a-zA-Z][a-zA-Z0-9_]*"; // im original ist kein _
	}
}

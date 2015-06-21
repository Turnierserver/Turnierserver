package org.pixelgaffer.katepartparser.context;

import lombok.ToString;

@ToString(callSuper = true)
public class LineContinue extends RegExpr
{
	public LineContinue ()
	{
		String = "\\\\$";
	}
}

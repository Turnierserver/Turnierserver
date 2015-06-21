package org.pixelgaffer.katepartparser.context;

import lombok.ToString;

@ToString(callSuper = true)
public class Int extends RegExpr
{
	public Int ()
	{
		String = "[0123456789]+";
	}
}

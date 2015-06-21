package org.pixelgaffer.katepartparser.context;

import lombok.ToString;

@ToString(callSuper = true)
public class Float extends RegExpr
{
	public Float ()
	{
		String = "[0123456789]+\\.[0123456789]+";
	}
}

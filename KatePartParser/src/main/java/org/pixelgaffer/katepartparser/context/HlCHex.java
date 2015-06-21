package org.pixelgaffer.katepartparser.context;

import lombok.ToString;

@ToString(callSuper = true)
public class HlCHex extends RegExpr
{
	public HlCHex ()
	{
		String = "0x[0123456789abcdef]+(\\.[0123456789abcdef]+)?";
	}
}

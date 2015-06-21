package org.pixelgaffer.katepartparser.context;

import lombok.ToString;

@ToString(callSuper = true)
public class HlCStringChar extends RegExpr
{
	public HlCStringChar ()
	{
		String = "\\\\([abefnrtv\"'?\\\\]|x[0123456789abcdef]+|0[12345678]+)";
	}
}

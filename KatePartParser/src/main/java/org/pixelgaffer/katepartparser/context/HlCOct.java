package org.pixelgaffer.katepartparser.context;

import lombok.ToString;

@ToString(callSuper = true)
public class HlCOct extends RegExpr
{
	public HlCOct ()
	{
		String = "0[012345678]+(\\.[012345678]+)?";
	}
}

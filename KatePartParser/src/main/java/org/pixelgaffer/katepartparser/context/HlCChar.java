package org.pixelgaffer.katepartparser.context;

import lombok.ToString;

@ToString(callSuper = true)
public class HlCChar extends RegExpr
{
	public HlCChar ()
	{
		String = "'\\\\([abefnrtv\"'?\\\\]|x[0123456789abcdef]+|0[12345678]+)'";
	}
}

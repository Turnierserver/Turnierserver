package org.pixelgaffer.katepartparser.context;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class Detect2Chars extends DefaultRule
{
	@Getter
	public char character;
	
	@Getter
	public char char1;

	@Override
	public int matches (String line, int off, Map<String, List<String>> lists)
	{
		if (line.length() <= off + 1)
			return 0;
		if ((line.charAt(off) == character) && (line.charAt(off + 1) == char1))
			return 2;
		return 0;
	}
}

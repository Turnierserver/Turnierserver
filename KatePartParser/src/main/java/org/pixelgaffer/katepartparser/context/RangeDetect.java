package org.pixelgaffer.katepartparser.context;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class RangeDetect extends DefaultRule
{
	@Getter
	public char character;
	
	@Getter
	public char char1;

	@Override
	public int matches (String line, int off, Map<String, List<String>> lists)
	{
		if (line.charAt(off) != character)
			return 0;
		int end = line.indexOf(char1, off + 1);
		if (end == -1)
			return 0;
		return (end - off);
	}
}

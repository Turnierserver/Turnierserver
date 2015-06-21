package org.pixelgaffer.katepartparser.context;

import java.util.List;
import java.util.Map;

import lombok.ToString;

@ToString(callSuper = true)
public class DetectSpaces extends DefaultRule
{
	@Override
	public int matches (String line, int off, Map<String, List<String>> lists)
	{
		int start = 0;
		while (Character.isWhitespace(line.charAt(off + start)))
			start++;
		return start;
	}
}

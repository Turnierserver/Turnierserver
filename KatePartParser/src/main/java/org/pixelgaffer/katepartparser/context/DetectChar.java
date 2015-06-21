package org.pixelgaffer.katepartparser.context;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class DetectChar extends DefaultRule
{
	@Getter
	public char character;

	@Override
	public int matches (String line, int off, Map<String, List<String>> lists)
	{
		if (line.charAt(off) == character)
			return 1;
		return 0;
	}
}

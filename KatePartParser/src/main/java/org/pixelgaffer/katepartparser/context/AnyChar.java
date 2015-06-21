package org.pixelgaffer.katepartparser.context;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class AnyChar extends DefaultRule
{
	@Getter
	public String String;
	
	@Override
	public int matches (String line, int off, Map<String, List<String>> lists)
	{
		if (String.contains(Character.toString(line.charAt(off))))
			return 1;
		return 0;
	}
}

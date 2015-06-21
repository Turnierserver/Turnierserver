package org.pixelgaffer.katepartparser.context;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class WordDetect extends DefaultRule
{
	public static final String DELIMETERS = "\\.\\(\\):!\\+,\\-<=>%&\\/;\\?\\[\\]^\\{|\\}~\\\\\\* \t";
	
	@Getter
	public String String;
	
	@Getter
	public boolean insensitive = false;
	
	@Override
	public int matches (String line, int off, Map<String, List<String>> lists)
	{
		if (line.length() <= String.length() + off)
			return 0;
		if ((off > 0) && !DELIMETERS.contains(line.substring(off - 1, off)))
			return 0;
		if ((line.length() <= String.length() + off + 1)
				&& !DELIMETERS.contains(line.substring(off + String.length() - 1, off + String.length())))
			return 0;
		
		String substr = line.substring(off, String.length() + off);
		if (insensitive)
		{
			if (String.equalsIgnoreCase(substr))
				return String.length();
		}
		else if (String.equals(substr))
			return String.length();
		return 0;
	}
}

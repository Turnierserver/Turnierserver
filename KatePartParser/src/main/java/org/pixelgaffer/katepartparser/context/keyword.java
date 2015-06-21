package org.pixelgaffer.katepartparser.context;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class keyword extends DefaultRule
{
	@Getter
	public String String;
	
	@Override
	public int matches (String line, int off, Map<String, List<String>> lists)
	{
		if (off > 0 && !WordDetect.DELIMETERS.contains(line.substring(off - 1, off)))
			return 0;
		String substr = line.substring(off);
		String words[] = substr.split("[" + WordDetect.DELIMETERS + "]");
		if (words.length < 1)
			return 0;
		if (lists.get(String).contains(words[0]))
			return words[0].length();
		return 0;
	}
}

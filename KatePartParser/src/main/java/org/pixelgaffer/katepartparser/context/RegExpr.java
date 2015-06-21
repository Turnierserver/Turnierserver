package org.pixelgaffer.katepartparser.context;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true, exclude = { "p" })
public class RegExpr extends DefaultRule
{
	@Getter
	public String String;
	
	@Getter
	public boolean insensitive = false;
	
	@Getter
	public boolean minimal = false;

	private Pattern p = null;
	
	@Override
	public int matches (String line, int off, Map<String, List<String>> lists)
	{
		if (p == null)
			p = insensitive ? Pattern.compile(String, Pattern.CASE_INSENSITIVE) : Pattern.compile(String);
		Matcher m = p.matcher(line);
		if (m.find(off))
		{
			if (m.start() == off)
				return (m.end() - m.start());
		}
		return 0;
	}
}

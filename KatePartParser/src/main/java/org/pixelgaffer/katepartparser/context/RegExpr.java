/*
 * RegExpr.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

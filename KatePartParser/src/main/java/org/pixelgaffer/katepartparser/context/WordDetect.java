/*
 * WordDetect.java
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

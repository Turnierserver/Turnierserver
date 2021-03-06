/*
 * keyword.java
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

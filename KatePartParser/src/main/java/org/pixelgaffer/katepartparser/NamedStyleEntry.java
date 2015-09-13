/*
 * NamedStyleEntry.java
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
package org.pixelgaffer.katepartparser;

import lombok.Getter;
import lombok.Setter;

public class NamedStyleEntry extends StyleEntry
{
	public NamedStyleEntry ()
	{
		super();
	}
	
	public NamedStyleEntry (String color, String bgColor, boolean italic, boolean bold, boolean underline,
							boolean strikeout, boolean spellChecking)
	{
		super(color, bgColor, italic, bold, underline, strikeout, spellChecking);
	}
	
	public NamedStyleEntry (StyleEntry other)
	{
		super(other);
	}
	
	@Getter
	@Setter
	private String name;
	
	public String toCss (boolean readable)
	{
		return toCss(getName(), readable);
	}
}

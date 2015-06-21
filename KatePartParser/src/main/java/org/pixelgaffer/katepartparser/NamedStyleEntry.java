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

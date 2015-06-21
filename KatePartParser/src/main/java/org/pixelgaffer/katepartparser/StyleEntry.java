package org.pixelgaffer.katepartparser;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class StyleEntry implements Cloneable
{
	@Getter
	@Setter
	private String color;
	
	@Getter
	@Setter
	private String bgColor;
	
	@Getter
	@Setter
	private boolean italic;
	
	@Getter
	@Setter
	private boolean bold;
	
	@Getter
	@Setter
	private boolean underline;
	
	@Getter
	@Setter
	private boolean strikeout;
	
	@Getter
	@Setter
	private boolean spellChecking;
	
	public String toCss (String clazz, boolean readable)
	{
		StringBuilder css = new StringBuilder();
		if (clazz != null)
			css.append(".").append(clazz).append(readable ? " " : "").append("{");
		css.append(readable ? "\n\t" : "").append("-fx-fill:").append(readable ? " " : "").append(color).append(";");
		css.append(readable ? "\n\t" : "").append("-fx-background-color:").append(readable ? " " : "").append(bgColor).append(";");
		css.append(readable ? "\n\t" : "").append("-fx-font-style:").append(readable ? " " : "").append(italic ? "italic" : "normal").append(";");
		css.append(readable ? "\n\t" : "").append("-fx-font-weight:").append(readable ? " " : "").append(bold ? "bold" : "normal").append(";");
		css.append(readable ? "\n\t" : "").append("-fx-underline:").append(readable ? " " : "").append(underline).append(";");
		css.append(readable ? "\n\t" : "").append("-fx-strikethrough:").append(readable ? " " : "").append(strikeout).append(";");
		if (clazz != null)
			css.append(readable ? "\n" : "").append("}");
		return css.toString();
	}
	
	// clone ctor
	public StyleEntry (StyleEntry other)
	{
		setColor(other.getColor());
		setBgColor(other.getBgColor());
		setItalic(other.isItalic());
		setBold(other.isBold());
		setUnderline(other.isUnderline());
		setStrikeout(other.isStrikeout());
		setSpellChecking(other.isSpellChecking());
	}
	
	// clone method
	@Override
	public StyleEntry clone ()
	{
		return new StyleEntry(this);
	}
}

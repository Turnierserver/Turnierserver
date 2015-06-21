package org.pixelgaffer.katepartparser.context;

import lombok.Getter;
import lombok.ToString;

@ToString
public abstract class DefaultRule implements ContextRule
{
	@Getter
	public String attribute;
	
	@Getter
	public String context = "#stay";
	
	@Getter
	public String beginRegion;
	
	@Getter
	public String endRegion;
	
	@Getter
	public boolean lookAhead = false;
	
	// TODO wenigstens das sollte beachtet werden
	@Getter
	public boolean firstNonSpace = false;
	
	@Getter
	public String column;
	
	@Getter
	public boolean dynamic;
}

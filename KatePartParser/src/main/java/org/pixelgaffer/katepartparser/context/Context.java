package org.pixelgaffer.katepartparser.context;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = { "rules" })
@RequiredArgsConstructor
public class Context
{
	@Getter
	@NonNull
	private String name;
	
	@Getter
	@NonNull
	private String attribute;
	
	@Getter
	@NonNull
	private String lineEndContext;
	
	@Getter
	@Setter
	private boolean fallthrough;
	
	@Getter
	@Setter
	private String fallthroughContext;
	
	@Getter
	private final List<ContextRule> rules = new ArrayList<>();
}

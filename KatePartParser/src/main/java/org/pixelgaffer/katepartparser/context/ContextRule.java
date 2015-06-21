package org.pixelgaffer.katepartparser.context;

import java.util.List;
import java.util.Map;

public interface ContextRule
{
	/**
	 * Gibt die Anzahl an Zeichen zurück für die diese Regel passt.
	 */
	public int matches (String line, int off, Map<String, List<String>> lists);
	
	/**
	 * Gibt das Attribut zur Darstellung zurück.
	 */
	public String getAttribute();
	
	/**
	 * Gibt den nächsten Context oder #stay zurück.
	 */
	public String getContext();
}

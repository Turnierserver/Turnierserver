package org.pixelgaffer.katepartparser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.json.JSONObject;

@NoArgsConstructor
public class Styles
{
	@RequiredArgsConstructor
	private static class ParsedStyle implements Style
	{
		@NonNull
		@Getter
		private StyleEntry normal;
		
		@Getter
		private final Map<String, StyleEntry> entries = new HashMap<>();
		
		public StyleEntry getAlert() { return entries.get("alert"); }
		public StyleEntry getAnnotation() { return entries.get("annotation"); }
		public StyleEntry getAttribute() { return entries.get("attribute"); }
		public StyleEntry getBaseN() { return entries.get("baseN"); }
		public StyleEntry getBuiltIn() { return entries.get("builtIn"); }
		public StyleEntry getChar() { return entries.get("char"); }
		public StyleEntry getComment() { return entries.get("comment"); }
		public StyleEntry getCommentVar() { return entries.get("commentVar"); }
		public StyleEntry getConstant() { return entries.get("constant"); }
		public StyleEntry getControlFlow() { return entries.get("controlFlow"); }
		public StyleEntry getDataType() { return entries.get("dataType"); }
		public StyleEntry getDecVal() { return entries.get("decVal"); }
		public StyleEntry getDocumentation() { return entries.get("documentation"); }
		public StyleEntry getError() { return entries.get("error"); }
		public StyleEntry getExtension() { return entries.get("extension"); }
		public StyleEntry getFloat() { return entries.get("float"); }
		public StyleEntry getFunction() { return entries.get("function"); }
		public StyleEntry getImport() { return entries.get("import"); }
		public StyleEntry getInformation() { return entries.get("information"); }
		public StyleEntry getKeyword() { return entries.get("keyword"); }
		public StyleEntry getOperator() { return entries.get("operator"); }
		public StyleEntry getOthers() { return entries.get("others"); }
		public StyleEntry getPreprocessor() { return entries.get("preprocessor"); }
		public StyleEntry getSpecialChar() { return entries.get("specialChar"); }
		public StyleEntry getSpecialString() { return entries.get("specialString"); }
		public StyleEntry getString() { return entries.get("string"); }
		public StyleEntry getRegionMarker() { return entries.get("regionMarker"); }
		public StyleEntry getVariable() { return entries.get("variable"); }
		public StyleEntry getVerbatimString() { return entries.get("verbatimString"); }
		public StyleEntry getWarning() { return entries.get("warning"); }
	}
	
	public static Style getStyle (String name) throws IOException
	{
		return parseStyle(new InputStreamReader(Styles.class.getResourceAsStream("styles/" + name + ".json")));
	}
	
	public static Style parseStyle (Reader in) throws IOException
	{
		String str = "";
		int read;
		char buf[] = new char[8192];
		while ((read = in.read(buf)) > 0)
			str += new String(buf, 0, read);
		in.close();
		
		JSONObject obj = new JSONObject(str);
		JSONObject normalObj = obj.getJSONObject("normal");
		StyleEntry normal = new StyleEntry(
				normalObj.getString("color"), normalObj.getString("bgColor"),
				normalObj.getBoolean("italic"), normalObj.getBoolean("bold"),
				normalObj.getBoolean("underline"), normalObj.getBoolean("strikeout"),
				normalObj.getBoolean("spellChecking"));
		ParsedStyle style = new ParsedStyle(normal);
		for (String key : obj.keySet())
		{
			StyleEntry entry = new StyleEntry(normal);
			JSONObject entryObj = obj.getJSONObject(key);
			if (entryObj.has("bgColor"))
				entry.setBgColor(entryObj.getString("bgColor"));
			if (entryObj.has("color"))
				entry.setColor(entryObj.getString("color"));
			if (entryObj.has("bgColor"))
				entry.setBgColor(entryObj.getString("bgColor"));
			if (entryObj.has("italic"))
				entry.setItalic(entryObj.getBoolean("italic"));
			if (entryObj.has("bold"))
				entry.setBold(entryObj.getBoolean("bold"));
			if (entryObj.has("underline"))
				entry.setUnderline(entryObj.getBoolean("underline"));
			if (entryObj.has("strikeout"))
				entry.setStrikeout(entryObj.getBoolean("strikeout"));
			if (entryObj.has("spellChecking"))
				entry.setSpellChecking(entryObj.getBoolean("spellChecking"));
			style.getEntries().put(key, entry);
		}
		return style;
	}
}

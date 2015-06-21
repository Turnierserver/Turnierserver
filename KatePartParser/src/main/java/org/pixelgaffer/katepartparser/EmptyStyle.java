package org.pixelgaffer.katepartparser;

public class EmptyStyle implements Style
{
	public StyleEntry getNormal() { return new StyleEntry("#000000", "#ffffff", false, false, false, false, false); }
	
	public StyleEntry getAlert() { return getNormal(); }
	public StyleEntry getAnnotation() { return getNormal(); }
	public StyleEntry getAttribute() { return getNormal(); }
	public StyleEntry getBaseN() { return getNormal(); }
	public StyleEntry getBuiltIn() { return getNormal(); }
	public StyleEntry getChar() { return getNormal(); }
	public StyleEntry getComment() { return getNormal(); }
	public StyleEntry getCommentVar() { return getNormal(); }
	public StyleEntry getConstant() { return getNormal(); }
	public StyleEntry getControlFlow() { return getNormal(); }
	public StyleEntry getDataType() { return getNormal(); }
	public StyleEntry getDecVal() { return getNormal(); }
	public StyleEntry getDocumentation() { return getNormal(); }
	public StyleEntry getError() { return getNormal(); }
	public StyleEntry getExtension() { return getNormal(); }
	public StyleEntry getFloat() { return getNormal(); }
	public StyleEntry getFunction() { return getNormal(); }
	public StyleEntry getImport() { return getNormal(); }
	public StyleEntry getInformation() { return getNormal(); }
	public StyleEntry getKeyword() { return getNormal(); }
	public StyleEntry getOperator() { return getNormal(); }
	public StyleEntry getOthers() { return getNormal(); }
	public StyleEntry getPreprocessor() { return getNormal(); }
	public StyleEntry getSpecialChar() { return getNormal(); }
	public StyleEntry getSpecialString() { return getNormal(); }
	public StyleEntry getString() { return getNormal(); }
	public StyleEntry getRegionMarker() { return getNormal(); }
	public StyleEntry getVariable() { return getNormal(); }
	public StyleEntry getVerbatimString() { return getNormal(); }
	public StyleEntry getWarning() { return getNormal(); }
}

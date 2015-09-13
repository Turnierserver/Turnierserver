/*
 * Style.java
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// http://kate-editor.org/2014/03/07/kate-part-kf5-new-default-styles-for-better-color-schemes/
public interface Style
{
	public StyleEntry getAlert();
	public StyleEntry getAnnotation();
	public StyleEntry getAttribute();
	public StyleEntry getBaseN();
	public StyleEntry getBuiltIn();
	public StyleEntry getChar();
	public StyleEntry getComment();
	public StyleEntry getCommentVar();
	public StyleEntry getConstant();
	public StyleEntry getControlFlow();
	public StyleEntry getDataType();
	public StyleEntry getDecVal();
	public StyleEntry getDocumentation();
	public StyleEntry getError();
	public StyleEntry getExtension();
	public StyleEntry getFloat();
	public StyleEntry getFunction();
	public StyleEntry getImport();
	public StyleEntry getInformation();
	public StyleEntry getKeyword();
	public StyleEntry getNormal();
	public StyleEntry getOperator();
	public StyleEntry getOthers();
	public StyleEntry getPreprocessor();
	public StyleEntry getSpecialChar();
	public StyleEntry getSpecialString();
	public StyleEntry getString();
	public StyleEntry getRegionMarker();
	public StyleEntry getVariable();
	public StyleEntry getVerbatimString();
	public StyleEntry getWarning();
	
	public default StyleEntry getEntry (String name)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException
	{
		if (name.startsWith("ds"))
			name = name.substring(2);
		if (Character.isLowerCase(name.charAt(0)))
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		name = "get" + name;
		Method m = getClass().getMethod(name);
		return (StyleEntry) m.invoke(this);
	}
}

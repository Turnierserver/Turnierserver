/*
 * Exceptions.java
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
package org.pixelgaffer.turnierserver.codr.utilities;


import javafx.collections.ObservableList;


/**
 * Stellt mehrere Exceptions bereit, von denen manche als Rückgabetypen "missbraucht" werden.
 * 
 * @author Philip
 */
public final class Exceptions {
	
	private Exceptions() {
	}
	
	
	public static class NothingDoneException extends Exception {
		
		private static final long serialVersionUID = 2490608616108332698L;
	}
	
	
	public static class NewException extends Exception {
		
		private static final long serialVersionUID = 8433430348371632406L;
		public ObservableList<String> newValues;
		
		
		public NewException(ObservableList<String> newValues) {
			this.newValues = newValues;
		}
	}
	
	
	public static class DeletedException extends NewException {
		
		private static final long serialVersionUID = 3986670283756751273L;
		
		
		public DeletedException(ObservableList<String> newValues) {
			super(newValues);
		}
	}
	
	
	public static class UpdateException extends Exception {
		
		private static final long serialVersionUID = -764342717934270870L;
	}
	
	
	public static class CompileException extends Exception {
		
		private static final long serialVersionUID = 7293514312919797514L;
		public String compileOutput;
		
		
		public CompileException(String compileOutput) {
			this.compileOutput = compileOutput;
		}
	}
	
}

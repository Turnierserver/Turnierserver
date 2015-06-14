package org.pixelgaffer.turnierserver.codr.utilities;


import javafx.collections.ObservableList;



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

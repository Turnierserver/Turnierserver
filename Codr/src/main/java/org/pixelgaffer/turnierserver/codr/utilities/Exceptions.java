package org.pixelgaffer.turnierserver.codr.utilities;


import javafx.collections.ObservableList;



public final class Exceptions {
	
	private Exceptions() {
	}
	
	
	public static class NothingDoneException extends Exception {
	}
	
	public static class NewException extends Exception {
		
		public ObservableList<String> newValues;
		
		
		public NewException(ObservableList<String> newValues) {
			this.newValues = newValues;
		}
	}
	
	public static class DeletedException extends NewException {
		
		public DeletedException(ObservableList<String> newValues) {
			super(newValues);
		}
	}
	
	public static class UpdateException extends Exception {
	}
	
	public static class CompileException extends Exception {
		public String compileOutput;
		public CompileException(String compileOutput) {
			this.compileOutput = compileOutput;
		}
	}
	
}

package org.pixelgaffer.turnierserver.esu.utilities;

import javafx.collections.ObservableList;

public final class Exceptions {
	
	private Exceptions() {}
	
	public static class NothingDoneException extends RuntimeException {}
	
	public static class NewException extends RuntimeException {
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
	public static class UpdateException extends RuntimeException {}
	
}

package org.pixelgaffer.turnierserver.codr.view;


import org.pixelgaffer.turnierserver.codr.MainApp;



public class ControllerSubmission {
	
	MainApp mainApp;
	
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app) {
		mainApp = app;
		MainApp.cSubmission = this;
	}
}

package org.pixelgaffer.turnierserver.codr.view;


import org.pixelgaffer.turnierserver.codr.MainApp;

import javafx.fxml.FXML;



public class ControllerSettings {
	
	MainApp mainApp;
	
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app) {
		mainApp = app;
		mainApp.cSettings = this;
	}
}

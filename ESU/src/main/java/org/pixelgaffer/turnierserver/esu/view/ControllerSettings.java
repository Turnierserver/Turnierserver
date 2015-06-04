package org.pixelgaffer.turnierserver.esu.view;

import org.pixelgaffer.turnierserver.esu.MainApp;
import javafx.fxml.FXML;

public class ControllerSettings {

	MainApp mainApp;

	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app){
		mainApp = app;
		mainApp.cSettings = this;
	}
}

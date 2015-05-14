package org.pixelgaffer.turnierserver.esu.view;

import org.pixelgaffer.turnierserver.esu.MainApp;
import javafx.fxml.FXML;

public class ControllerRanking {

	MainApp mainApp;

	public void setMainApp(MainApp app){
		mainApp = app;
		mainApp.cRanking = this;
	}
}

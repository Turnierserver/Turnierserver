/*
 * ControllerRoot.java
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
package org.pixelgaffer.turnierserver.codr.view;


import java.io.IOException;

import org.pixelgaffer.turnierserver.codr.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;



public class ControllerRoot {
	
	@FXML public TabPane tabPane;
	@FXML public Tab tabStartPage;
	@FXML public Tab tabKiManagement;
	@FXML public Tab tabGameManagement;
	@FXML public Tab tabRanking;
	@FXML public Tab tabSubmission;
	AnchorPane anchorStartPage;
	AnchorPane anchorKiManagement;
	AnchorPane anchorGameManagement;
	AnchorPane anchorRanking;
	AnchorPane anchorSubmission;
	
	MainApp mainApp;
	
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app) {
		mainApp = app;
		MainApp.cRoot = this;
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("StartPage.fxml"));
			anchorStartPage = (AnchorPane) loader.load();
			tabStartPage.setContent(anchorStartPage);
			((ControllerStartPage) loader.getController()).setMainApp(mainApp);

			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("AiManagement.fxml"));
			anchorKiManagement = (AnchorPane) loader.load();
			tabKiManagement.setContent(anchorKiManagement);
			((ControllerAiManagement) loader.getController()).setMainApp(mainApp);
			
			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("GameManagement.fxml"));
			anchorGameManagement = (AnchorPane) loader.load();
			tabGameManagement.setContent(anchorGameManagement);
			((ControllerGameManagement) loader.getController()).setMainApp(mainApp);
			
			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("Ranking.fxml"));
			anchorRanking = (AnchorPane) loader.load();
			tabRanking.setContent(anchorRanking);
			((ControllerRanking) loader.getController()).setMainApp(mainApp);
			
			loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("Submission.fxml"));
			anchorSubmission = (AnchorPane) loader.load();
			tabSubmission.setContent(anchorSubmission);
			((ControllerSubmission) loader.getController()).setMainApp(mainApp);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tabPane.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue.getText().equals("Rangliste")){
				if (MainApp.cRanking.ai == null && MainApp.cRanking.tvAis.getItems().size() > 0){
					MainApp.cRanking.tvAis.getSelectionModel().select(0);
				}
			}
		});
		
	}
	
	
}

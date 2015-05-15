package org.pixelgaffer.turnierserver.esu.view;


import java.util.ResourceBundle;
import java.io.IOException;
import java.net.URL;

import org.pixelgaffer.turnierserver.esu.*;
import org.pixelgaffer.turnierserver.esu.view.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

public class ControllerRoot{
	
	@FXML TabPane tabPane;
	@FXML Tab tabStartPage;
	@FXML Tab tabKiManagement;
	@FXML Tab tabGameManagement;
	@FXML Tab tabRanking;
	@FXML Tab tabSubmission;
	AnchorPane anchorStartPage;
	AnchorPane anchorKiManagement;
	AnchorPane anchorGameManagement;
	AnchorPane anchorRanking;
	AnchorPane anchorSubmission;
	
	MainApp mainApp;
	
	public void setMainApp(MainApp app){
		mainApp = app;
		mainApp.cRoot = this;
		
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
	}
	

}

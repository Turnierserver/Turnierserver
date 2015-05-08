package org.pixelgaffer.turnierserver.esu.view;


import java.util.ResourceBundle;
import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

public class ControllerRoot implements Initializable{
	
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
	
	public void initialize(URL url, ResourceBundle resourceBundle){
		
		try {
			anchorStartPage = (AnchorPane) FXMLLoader.load(getClass().getResource("StartPage.fxml"));
			tabStartPage.setContent(anchorStartPage);
			anchorKiManagement = (AnchorPane) FXMLLoader.load(getClass().getResource("KiManagement.fxml"));
			tabKiManagement.setContent(anchorKiManagement);
			anchorGameManagement = (AnchorPane) FXMLLoader.load(getClass().getResource("GameManagement.fxml"));
			tabGameManagement.setContent(anchorGameManagement);
			anchorRanking = (AnchorPane) FXMLLoader.load(getClass().getResource("Ranking.fxml"));
			tabRanking.setContent(anchorRanking);
			anchorSubmission = (AnchorPane) FXMLLoader.load(getClass().getResource("Submission.fxml"));
			tabSubmission.setContent(anchorSubmission);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

package org.pixelgaffer.turnierserver.esu.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.pixelgaffer.turnierserver.esu.MainApp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class ControllerAiManagement{
	

	MainApp mainApp;
	@FXML public Button btAbort;
	@FXML public Button btEdit;
	@FXML public Button btNewVersion;
	@FXML public Button btCompile;
	@FXML public Button btQualify;
	@FXML public Button btFinish;
	@FXML public Button btUpload;
	@FXML public Label lbName;
	@FXML public Label lbLanguage;
	@FXML public Label lbCompiled;
	@FXML public Label lbQualified;
	@FXML public Label lbFinished;
	@FXML public Label lbUploaded;
	@FXML public RadioButton rbSimple;
	@FXML public RadioButton rbContinue;
	@FXML public RadioButton rbFromFile;
	@FXML public TextField tbFile;
	@FXML public TextField tbName;
	@FXML public TextArea tbOutput;
	@FXML public TextArea tbDescription;
	@FXML public ChoiceBox<String> cbVersion;
	@FXML public ChoiceBox<String> cbLanguage;
	@FXML public ListView<String> lvKis;
	@FXML public ImageView image;
	@FXML public TabPane tpCode;
	


	public void setMainApp(MainApp app){
		mainApp = app;
		mainApp.cAi = this;
	}
	
	
	@FXML void clickNewKi(){
		tbFile.setText("Info1 geklickt");
	}
	
	@FXML void clickAbort(){
		tbFile.setText("Info2 geklickt");
	}
	
	@FXML void clickEdit(){
		tbFile.setText("Info3 geklickt");
	}
	
	@FXML void clickToActual(){
		tbFile.setText("Info4 geklickt");
	}
	
	@FXML void clickVersionChange(){
		tbFile.setText("Info5 geklickt");
	}
	
	@FXML void clickRbSimple(){
		tbFile.setText("Info6 geklickt");
	}
	
	@FXML void clickRbContinue(){
		tbFile.setText("Info7 geklickt");
	}
	
	@FXML void clickRbFromFile(){
		tbFile.setText("Info8 geklickt");
	}
	
	@FXML void clickSelectFile(){
		tbFile.setText("Info9 geklickt");
	}
	
	@FXML void clickNewVersion(){
		tbFile.setText("Info10 geklickt");
	}
	
	@FXML void clickCompile(){
		tbFile.setText("Info11 geklickt");
	}
	
	@FXML void clickQualify(){
		tbFile.setText("Info12 geklickt");
	}
	
	@FXML void clickFinish(){
		tbFile.setText("Info13 geklickt");
	}
	
	@FXML void clickUpload(){
		tbFile.setText("Info14 geklickt");
	}
	
	@FXML void clickChangeImage(){
		tbFile.setText("Info15 geklickt");
	}
	
}

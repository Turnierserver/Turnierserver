package org.pixelgaffer.turnierserver.esu.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

public class ControllerKiManagement implements Initializable{
	
	
	@FXML Button btAbort;
	@FXML Button btEdit;
	@FXML Button btNewVersion;
	@FXML Button btCompile;
	@FXML Button btQualify;
	@FXML Button btFreeze;
	@FXML Button btUpload;
	@FXML Label lbName;
	@FXML Label lbLanguage;
	@FXML Label lbCompiled;
	@FXML Label lbQualified;
	@FXML Label lbFrozen;
	@FXML Label lbUploaded;
	@FXML RadioButton rbSimple;
	@FXML RadioButton rbContinue;
	@FXML RadioButton rbFromFile;
	@FXML TextField tbFile;
	@FXML TextField tbName;
	@FXML TextArea tbOutput;
	@FXML TextArea tbDescription;
	@FXML ChoiceBox<String> cbVersion;
	@FXML ChoiceBox<String> cbLanguage;
	@FXML ListView<String> lvKis;
	@FXML ImageView image;
	@FXML TabPane tpCode;
	

	public void initialize(URL url, ResourceBundle resourceBundle){
		
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
	
	@FXML void clickFreeze(){
		tbFile.setText("Info13 geklickt");
	}
	
	@FXML void clickUpload(){
		tbFile.setText("Info14 geklickt");
	}
	
	@FXML void clickChangeImage(){
		tbFile.setText("Info15 geklickt");
	}
	
}

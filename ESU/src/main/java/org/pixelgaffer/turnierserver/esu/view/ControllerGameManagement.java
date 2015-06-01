package org.pixelgaffer.turnierserver.esu.view;

import org.pixelgaffer.turnierserver.esu.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ControllerGameManagement {
	
	
	@FXML public Label lbMode;
	@FXML public Label lbDate;
	@FXML public Label lbDuration;
	@FXML public Label lbLogic;
	@FXML public Label lbStartState;
	@FXML public Label lbChallenger;
	@FXML public Label lbJudged;
	@FXML public ToggleButton btOffline;
	@FXML public ToggleButton btOnline;
	@FXML public ChoiceBox<String> cbLogic;
	@FXML public ChoiceBox<String> cbStartState;
	@FXML public ListView<Player> lvPlayer1;
	@FXML public ListView<Player> lvPlayer2;
	@FXML public ProgressIndicator progress;
	@FXML public Button btLoadOnline;
	@FXML public TextArea tbOutput1;
	@FXML public TextArea tbOutput2;
	@FXML public TableView<ParticipantResult> tableResult;
	
	MainApp mainApp;
	
	Game game = null;
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app){
		mainApp = app;
		mainApp.cGame = this;
	}
	
	
	public void showGame(Game ggame){
		
	}
	
	public void showGame(){
		if (game != null){
			
		}
		else{
			lbMode.setText("Offline");
			lbDate.setText("Jetzt");
			lbDuration.setText("555ms");
			lbLogic.setText("Test-Logik");
		}
	}
	
	

	@FXML
	void clickChallenger(){
		tbOutput1.setText("Info1 geklickt");
	}

	@FXML
	void clickOnline(){
		tbOutput1.setText("Info2 geklickt");
	}

	@FXML
	void clickOffline(){
		tbOutput1.setText("Info3 geklickt");
	}

	@FXML
	void clickStartGame(){
		tbOutput1.setText("Info4 geklickt");
	}

	@FXML
	void clickLoadSaved(){
		tbOutput1.setText("Info5 geklickt");
	}

	@FXML
	void clickLoadOnline(){
		tbOutput1.setText("Info6 geklickt");
	}
}

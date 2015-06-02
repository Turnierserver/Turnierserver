package org.pixelgaffer.turnierserver.esu.view;

import org.pixelgaffer.turnierserver.esu.Ai;
import org.pixelgaffer.turnierserver.esu.MainApp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class ControllerRanking {
	

	@FXML Label lbName;
	@FXML Label lbUser;
	@FXML Label lbElo;
	@FXML Label lbLanguage;
	@FXML Button btChallenge;
	@FXML TextArea tbDescription;
	@FXML TableView<Ai> tvAis;
	@FXML TableView<Ai> tvVersions;
	
	MainApp mainApp;
	Ai ai;
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app){
		mainApp = app;
		mainApp.cRanking = this;
		tvAis.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ai>() {
		    @Override
		    public void changed(ObservableValue<? extends Ai> observable, Ai oldValue, Ai newValue) {
		        clickChangeAi(newValue);
		    }
		});
	}
	

	public void showAi(Ai aai){
		ai = aai;
	}
	public void showAi(){
		if (ai != null){
			lbName.setText(ai.title);
			tbDescription.setText(ai.description);
			lbUser.setText(ai.userName);
			lbElo.setText(ai.elo);
			lbLanguage.setText(ai.language.toString());
		}
		else{
			lbName.setText("Null");
			tbDescription.setText("Aktuell wird keine KI angezeigt");
			lbUser.setText("Keiner");
			lbElo.setText("1000");
			lbLanguage.setText("Java");
		}
	}
	
	
	@FXML public void clickChallenge(){
		
	}
	
	public void clickChangeAi(Ai selected){
		showAi(selected);
	}
	
	
}

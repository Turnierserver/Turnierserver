package org.pixelgaffer.turnierserver.esu.view;

import java.io.IOException;

import javax.xml.transform.ErrorListener;

import org.pixelgaffer.turnierserver.esu.MainApp;
import org.pixelgaffer.turnierserver.esu.Version;
import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Dialog;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ControllerStartPage{
	
	MainApp mainApp;
	@FXML Button btInfo;
	@FXML Button btRegister;
	@FXML Button btLogin;
	@FXML Hyperlink hlForgotPassword;
	@FXML TextField tbActualLogic;
	@FXML TextField tbEmail;
	@FXML PasswordField tbPassword;
	@FXML TitledPane tpLogic;
	@FXML TitledPane tpRegister;
	@FXML WebView wfNews;
	@FXML VBox vbLogin;
	@FXML GridPane gpLogin;
	@FXML Label lbLogin;
	@FXML Button btLogout;
	@FXML public ChoiceBox<String> cbGameTypes;
	@FXML Button btTryOnline;
	@FXML Label lbIsOnline;
	WebEngine webEngine;
	


	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app){
		mainApp = app;
		mainApp.cStart = this;

		webEngine = wfNews.getEngine();
		webEngine.setJavaScriptEnabled(true);
		webEngine.load("http://www.bundeswettbewerb-informatik.de/");
		wfNews.setZoom(0.9);
		
		updateLoggedIn();
		updateConnected();
		
		cbGameTypes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		        MainApp.actualGameType.set(newValue);
		        mainApp.aiManager.loadAis();
		    }
		});
		
		cbGameTypes.setItems(MainApp.gametypes);
		cbGameTypes.getSelectionModel().selectLast();
	}
	
	
	public void updateConnected(){
		if (mainApp.webConnector.ping()){
			lbIsOnline.setText("Es besteht eine Internetverbindung");
			btTryOnline.setText("nach Aktualisierungen suchen");
			vbLogin.setDisable(false);
		}else{
			lbIsOnline.setText("Momentan besteht keine Internetverbindung");
			btTryOnline.setText("Erneut versuchen");
			vbLogin.setDisable(true);
		}
	}
	
	public void updateLoggedIn(){
		if (mainApp.webConnector.isLoggedIn()){
			vbLogin.getChildren().clear();
			vbLogin.getChildren().add(lbLogin);
			vbLogin.getChildren().add(btLogout);
		}
		else{
			vbLogin.getChildren().clear();
			vbLogin.getChildren().add(lbLogin);
			vbLogin.getChildren().add(gpLogin);
		}
	}
	
	
	@FXML
	void clickInfo(){
		tbActualLogic.setText("Info geklickt");
	}
	
	@FXML
	void clickRegister(){
		tbActualLogic.setText("Registrieren geklickt");
	}
	
	@FXML
	void clickLogout(){
		try {
			mainApp.webConnector.logout();
		} catch (IOException e) {
			ErrorLog.write("Logout fehlgeschlagen");
		}
		updateLoggedIn();
	}
	
	@FXML
	void clickLogin(){
		
		try {
			if (!mainApp.webConnector.login(tbEmail.getText(), tbPassword.getText())){
				Dialog.error("Falsches Passwort oder Email", "Login fehlgeschlagen");
			}
			else{
				updateLoggedIn();
				Dialog.info("Login erfolgreich!");
			}
		} catch (IOException e) {
			Dialog.error("Login fehlgeschlagen: ERROR", "Login fehlgeschlagen");
			ErrorLog.write("Login fehlgeschlagen: " + e.getMessage());
		}
		updateLoggedIn();
	}
	
	@FXML
	void clickForgotPassword(){
		tbActualLogic.setText("Passwort vergessen geklickt");
	}
	
	@FXML
	void clickTryOnline(){
		updateConnected();
		mainApp.loadOnlineResources();
	}
}

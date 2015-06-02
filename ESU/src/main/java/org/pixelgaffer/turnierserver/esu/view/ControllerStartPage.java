package org.pixelgaffer.turnierserver.esu.view;

import java.io.IOException;

import javax.xml.transform.ErrorListener;

import org.pixelgaffer.turnierserver.esu.MainApp;
import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Dialog;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ControllerStartPage{
	
	MainApp mainApp;
	@FXML Button btInfo;
	@FXML Button btDownload;
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
	void clickDownload(){
		tbActualLogic.setText("Download geklickt");
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
	
}

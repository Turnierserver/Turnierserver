package org.pixelgaffer.turnierserver.codr.view;


import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;



public class ControllerStartPage {
	
	MainApp mainApp;
	@FXML Button btInfo;
	@FXML Button btRegister;
	@FXML Button btLogin;
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
	
	@FXML public ToggleButton btTheme;
	@FXML public Slider slFontSize;
	@FXML public Slider slScrollSpeed;
	@FXML public TextField tbPythonInterpreter;
	@FXML public TextField tbCplusplusCompiler;
	
	WebEngine webEngine;
	
	
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app) {
		mainApp = app;
		mainApp.cStart = this;
		
		webEngine = wfNews.getEngine();
		webEngine.setJavaScriptEnabled(true);
		webEngine.load("http://www.bundeswettbewerb-informatik.de/");
		wfNews.setZoom(0.9);
		
		updateLoggedIn();
		updateConnected();
		
		
		btTheme.selectedProperty().addListener(new ChangeListener<Boolean>() {
			
			@Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				clickTheme(newValue);
			}
		});
		
		cbGameTypes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				MainApp.actualGameType.set(newValue);
				mainApp.aiManager.loadAis();
			}
		});
		
		cbGameTypes.setItems(MainApp.gametypes);
		cbGameTypes.getSelectionModel().selectLast();
		
		tbPassword.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			
			@Override public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					clickLogin();
				}
				
			}
		});
	}
	
	
	public void updateConnected() {
		if (mainApp.webConnector.ping()) {
			lbIsOnline.setText("Es besteht eine Internetverbindung");
			btTryOnline.setText("nach Aktualisierungen suchen");
			vbLogin.setDisable(false);
		} else {
			lbIsOnline.setText("Momentan besteht keine Internetverbindung");
			btTryOnline.setText("Erneut versuchen");
			vbLogin.setDisable(true);
		}
	}
	
	
	public void updateLoggedIn() {
		if (mainApp.webConnector.isLoggedIn()) {
			vbLogin.getChildren().clear();
			vbLogin.getChildren().add(lbLogin);
			vbLogin.getChildren().add(btLogout);
		} else {
			vbLogin.getChildren().clear();
			vbLogin.getChildren().add(lbLogin);
			vbLogin.getChildren().add(gpLogin);
		}
	}
	
	
	@FXML void clickInfo() {
		
	}
	
	
	@FXML void clickRegister() {
		openWebPage("http://" + MainApp.webUrl + "/");
	}
	
	
	@FXML void clickLogout() {
		try {
			mainApp.webConnector.logout();
		} catch (IOException e) {
			ErrorLog.write("Logout fehlgeschlagen");
		}
		updateLoggedIn();
	}
	
	
	@FXML void clickLogin() {
		
		try {
			if (!mainApp.webConnector.login(tbEmail.getText(), tbPassword.getText())) {
				Dialog.error("Falsches Passwort oder Email", "Login fehlgeschlagen");
			} else {
				updateLoggedIn();
				Dialog.info("Login erfolgreich!");
			}
		} catch (IOException e) {
			Dialog.error("Login fehlgeschlagen: ERROR", "Login fehlgeschlagen");
			ErrorLog.write("Login fehlgeschlagen: " + e.getMessage());
		}
		updateLoggedIn();
	}
	
	
	@FXML void clickForgotPassword() {
		openWebPage("http://" + MainApp.webUrl + "/");
	}
	
	
	@FXML void clickTryOnline() {
		updateConnected();
		mainApp.loadOnlineResources();
	}
	
	
	@FXML void clickPythonInterpreter() {
		File result = Dialog.folderChooser(mainApp.stage, "Bitte einen Ordner auswählen");
		if (result != null)
			tbPythonInterpreter.setText(result.getPath());
	}
	
	
	@FXML void clickCplusplusCompiler() {
		File result = Dialog.folderChooser(mainApp.stage, "Bitte einen Ordner auswählen");
		if (result != null)
			tbCplusplusCompiler.setText(result.getPath());
	}
	
	
	
	void clickTheme(Boolean isSelected) {
		if (isSelected) {
			btTheme.setText("Dark");
		} else {
			btTheme.setText("Light");
		}
	}
	
	
	
	private void openWebPage(String url) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				URI uri = new URL(url).toURI();
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

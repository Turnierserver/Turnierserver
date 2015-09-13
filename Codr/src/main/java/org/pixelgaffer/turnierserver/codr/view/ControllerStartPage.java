package org.pixelgaffer.turnierserver.codr.view;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Settings;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class ControllerStartPage {
	
	MainApp mainApp;
	@FXML Button btInfo;
	@FXML Button btRegister;
	@FXML Button btLogin;
	@FXML public TextField tbEmail;
	@FXML PasswordField tbPassword;
	@FXML TitledPane tpLogic;
	@FXML TitledPane tpRegister;
	@FXML WebView wfNews;
	@FXML public VBox vbLogin;
	@FXML public GridPane gpLogin;
	@FXML public HBox hbLogout;
	@FXML public ChoiceBox<String> cbGameTypes;
	@FXML public Button btTryOnline;
	@FXML public Label lbIsOnline;
	
	@FXML public ProgressIndicator prOnlineResources;
	@FXML public ProgressIndicator prLogin;
	@FXML public ProgressIndicator prLogin1;
	
	@FXML public ToggleButton btTheme;
	@FXML public Slider slFontSize;
	@FXML public TextField tbPythonInterpreter;
	@FXML public TextField tbJDK;
	
	
	WebEngine webEngine;
	
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app) {
		mainApp = app;
		MainApp.cStart = this;
		
		webEngine = wfNews.getEngine();
		webEngine.setJavaScriptEnabled(true);
		webEngine.load("http://www.bundeswettbewerb-informatik.de/");
		wfNews.setZoom(0.9);
		
		MainApp.updateLoggedIn();
		MainApp.updateConnected();
		
		btTheme.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
			clickTheme(newValue);
		});
		
		
		cbGameTypes.setItems(MainApp.gametypes);
		
		tbPassword.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					clickLogin();
				}
				
			}
		});
		
	}
	
	
	public void loadAis() {
		MainApp.actualGameType.bindBidirectional(cbGameTypes.valueProperty());
		cbGameTypes.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			MainApp.aiManager.loadAis();
			MainApp.loadOnlineRanking();
			MainApp.cAi.lvAis.getSelectionModel().selectFirst();
		});
		cbGameTypes.getSelectionModel().selectLast();
	}
	
	
	@FXML
	void clickInfo() {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.open(new File("Downloads/" + MainApp.actualGameType.get() + "/info.pdf"));
			} catch (Exception e) {
				Dialog.error("Zu dem Spiel " + MainApp.actualGameType.get() + " gibt es keine info.pdf");
			}
		}
	}
	
	
	@FXML
	void clickRegister() {
		openWebPage("http://" + Settings.webUrl + "/");
	}
	
	
	@FXML
	void clickLogout() {
		Task<Boolean> updateL = new Task<Boolean>() {
			
			public Boolean call() {
				try {
					MainApp.webConnector.logout();
				} catch (IOException e) {
					return false;
				}
				return true;
			}
		};
		
		prLogin.setVisible(true);
		prLogin1.setVisible(true);
		
		updateL.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue) {
				MainApp.updateLoggedIn();
			} else {
				ErrorLog.write("Logout fehlgeschlagen");
			}
			prLogin.setVisible(false);
			prLogin1.setVisible(false);
		});
		
		Thread thread = new Thread(updateL, "updateLoggedIn");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	@FXML
	void clickLogin() {
		Task<String> updateL = new Task<String>() {
			
			public String call() {
				try {
					if (!MainApp.webConnector.login(tbEmail.getText(), tbPassword.getText())) {
						return "wrongPassword";
					} else {
						return "success";
					}
				} catch (IOException e) {
					return "error";
				}
			}
		};
		
		updateL.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			switch (newValue) {
			case "success":
				MainApp.updateLoggedIn();
				Dialog.info("Login erfolgreich!");
				break;
			case "wrongPassword":
				Dialog.error("Falsches Passwort oder Email", "Login fehlgeschlagen");
				break;
			case "error":
				Dialog.error("Login fehlgeschlagen: ERROR", "Login fehlgeschlagen");
				ErrorLog.write("Login fehlgeschlagen");
				break;
			}
		});
		prLogin.setVisible(true);
		
		Thread thread = new Thread(updateL, "updateLoggedIn");
		thread.setDaemon(true);
		thread.start();
		
		MainApp.updateLoggedIn();
	}
	
	
	@FXML
	void clickForgotPassword() {
		openWebPage("http://" + Settings.webUrl + "/");
	}
	
	
	@FXML
	void clickTryOnline() {
		MainApp.updateConnected();
		MainApp.loadOnlineResources();
	}
	
	
	@FXML
	void clickPythonInterpreter() {
		File result = Dialog.folderChooser(MainApp.stage, "Bitte einen Ordner auswählen");
		if (result != null)
			tbPythonInterpreter.setText(result.getPath());
	}
	
	
	@FXML
	void clickJDK() {
		File result = Dialog.folderChooser(MainApp.stage, "Bitte einen Ordner auswählen");
		if (result != null)
			tbJDK.setText(result.getPath());
	}
	
	
	void clickTheme(Boolean isSelected) {
		if (isSelected) {
			btTheme.setText("Dunkel");
		} else {
			btTheme.setText("Hell");
		}
		MainApp.cAi.showAi();
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

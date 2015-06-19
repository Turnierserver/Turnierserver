package org.pixelgaffer.turnierserver.codr.view;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

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

import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Settings;



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
	@FXML VBox vbLogin;
	@FXML GridPane gpLogin;
	@FXML HBox hbLogout;
	@FXML public ChoiceBox<String> cbGameTypes;
	@FXML Button btTryOnline;
	@FXML Label lbIsOnline;
	
	@FXML public ProgressIndicator prOnlineResources;
	@FXML public ProgressIndicator prLogin;
	@FXML public ProgressIndicator prLogin1;
	
	@FXML public ToggleButton btTheme;
	@FXML public Slider slFontSize;
	@FXML public TextField tbPythonInterpreter;
	@FXML public TextField tbCplusplusCompiler;
	@FXML public ChoiceBox<String> cbCplusplusCompilerType;
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
		
		updateLoggedIn();
		updateConnected();
		
		btTheme.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
			clickTheme(newValue);
		});
		
		cbGameTypes.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
			MainApp.actualGameType.set(newValue);
			MainApp.aiManager.loadAis();
		});
		
		cbGameTypes.setItems(MainApp.gametypes);
		cbGameTypes.getSelectionModel().selectLast();
		
		cbCplusplusCompilerType.getItems().add("gcc");
		cbCplusplusCompilerType.getItems().add("clang");
		cbCplusplusCompilerType.getItems().add("g++");
		cbCplusplusCompilerType.getSelectionModel().select(0);
		
		tbPassword.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			@Override public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					clickLogin();
				}
				
			}
		});
	}
	
	
	public void updateConnected() {
		Task<Boolean> updateC = new Task<Boolean>() {
			public Boolean call() {
				if (MainApp.webConnector.ping()) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		prOnlineResources.setVisible(true);
		
		updateC.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			prOnlineResources.setVisible(false);
			if (newValue) {
				lbIsOnline.setText("Es besteht eine Internetverbindung");
				btTryOnline.setText("nach Aktualisierungen suchen");
				vbLogin.setDisable(false);
			} else {
				lbIsOnline.setText("Momentan besteht keine Internetverbindung");
				btTryOnline.setText("Erneut versuchen");
				vbLogin.setDisable(true);
			}
		});
		
		Thread thread = new Thread(updateC, "updateConnected");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	public void updateLoggedIn() {
		
		Task<Boolean> updateL = new Task<Boolean>() {
			public Boolean call() {
				if (MainApp.webConnector.isLoggedIn()) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		prLogin.setVisible(true);
		prLogin1.setVisible(true);
		
		updateL.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue) {
				vbLogin.getChildren().clear();
				vbLogin.getChildren().add(hbLogout);
				if (MainApp.cGame == null)
					try {
						Thread.sleep(100);
					} catch (Exception e) {
					}
				if (MainApp.cGame != null) {
					MainApp.cGame.btOnline.setDisable(false);
				}
			} else {
				vbLogin.getChildren().clear();
				vbLogin.getChildren().add(gpLogin);
				if (MainApp.cGame == null)
					try {
						Thread.sleep(100);
					} catch (Exception e) {
					}
				if (MainApp.cGame != null) {
					MainApp.cGame.btOnline.setDisable(true);
				}
			}
			prLogin.setVisible(false);
			prLogin1.setVisible(false);
		});
		
		Thread thread = new Thread(updateL, "updateLoggedIn");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	@FXML void clickInfo() {
		
	}
	
	
	@FXML void clickRegister() {
		openWebPage("http://" + Settings.webUrl + "/");
	}
	
	
	@FXML void clickLogout() {
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
				updateLoggedIn();
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
	
	
	@FXML void clickLogin() {
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
				updateLoggedIn();
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
		
		updateLoggedIn();
	}
	
	
	@FXML void clickForgotPassword() {
		openWebPage("http://" + Settings.webUrl + "/");
	}
	
	
	@FXML void clickTryOnline() {
		updateConnected();
		mainApp.loadOnlineResources();
	}
	
	
	@FXML void clickPythonInterpreter() {
		File result = Dialog.folderChooser(MainApp.stage, "Bitte einen Ordner auswählen");
		if (result != null)
			tbPythonInterpreter.setText(result.getPath());
	}
	
	
	@FXML void clickCplusplusCompiler() {
		File result = Dialog.folderChooser(MainApp.stage, "Bitte einen Ordner auswählen");
		if (result != null)
			tbCplusplusCompiler.setText(result.getPath());
	}
	
	
	@FXML void clickJDK() {
		File result = Dialog.folderChooser(MainApp.stage, "Bitte einen Ordner auswählen");
		if (result != null)
			tbJDK.setText(result.getPath());
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

package org.pixelgaffer.turnierserver.codr;


import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.NewException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.NothingDoneException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.UpdateException;
import org.pixelgaffer.turnierserver.codr.utilities.Resources;
import org.pixelgaffer.turnierserver.codr.utilities.Settings;
import org.pixelgaffer.turnierserver.codr.utilities.WebConnector;
import org.pixelgaffer.turnierserver.codr.view.ControllerAiManagement;
import org.pixelgaffer.turnierserver.codr.view.ControllerGameManagement;
import org.pixelgaffer.turnierserver.codr.view.ControllerRanking;
import org.pixelgaffer.turnierserver.codr.view.ControllerRoot;
import org.pixelgaffer.turnierserver.codr.view.ControllerStartPage;
import org.pixelgaffer.turnierserver.codr.view.ControllerSubmission;



public class MainApp extends Application {
	
	public static Stage stage;
	public static ControllerRoot cRoot;
	public static ControllerStartPage cStart;
	public static ControllerAiManagement cAi;
	public static ControllerGameManagement cGame;
	public static ControllerRanking cRanking;
	public static ControllerSubmission cSubmission;
	
	public static Settings settings;
	
	
	public static WebConnector webConnector;
	public GameManager gameManager = new GameManager();
	public AiManager aiManager = new AiManager();
	
	public static StringProperty actualGameType = new SimpleStringProperty(null);
	public static ObservableList<String> gametypes = FXCollections.observableArrayList();
	public static ObservableList<String> languages = FXCollections.observableArrayList();
	
	public static ObservableList<AiBase> onlineAis = FXCollections.observableArrayList();
	public static ObservableList<AiBase> ownOnlineAis = FXCollections.observableArrayList();
	
	
	/**
	 * Main-Methode
	 * 
	 * @param args Argumente
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	
	
	/**
	 * start-Methode (wegen: extends Application)
	 */
	public void start(Stage _stage) throws Exception {
		ErrorLog.clear();
		ErrorLog.write("Programm startet...", true);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> exit()));
		
		stage = new Stage(StageStyle.DECORATED);
		
		settings = new Settings();
		settings.loadUrl();
		webConnector = new WebConnector("http://" + Settings.webUrl + "/api/");
		
		gametypes = webConnector.loadGametypesFromFile();
		languages = webConnector.loadLangsFromFile();
		
		CodeEditor.writeAce();
		
		if (gametypes == null || languages == null) {
			showSplashStage(_stage);
		} else {
			loadOnlineResources();
			showMainStage();
		}
		
		ErrorLog.write("Programm gestartet", true);
		
	}
	
	
	
	public void exit() {
		if (settings != null)
			settings.store(cStart);
		if (cAi != null && cAi.version != null)
			cAi.version.saveCode();
		
		if (cGame != null && cGame.runningGame != null) {
			try {
				cGame.runningGame.getGame().finishGame();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ErrorLog.write("Programm beendet", true);
	}
	
	
	public void loadOnlineResources() {
		final Task updateTask = new Task() {
			@Override protected Object call() throws InterruptedException {
				
				try {
					webConnector.updateGametypes();
				} catch (NewException e) {
					gametypes = e.newValues;
					updateMessage("neue Spieltypen");
				} catch (UpdateException e) {
				} catch (NothingDoneException e) {
				} catch (IOException e) {
				}
				
				try {
					webConnector.updateLanguages();
				} catch (NewException e) {
					languages = e.newValues;
					updateMessage("neue Sprachen");
				} catch (NothingDoneException e) {
				} catch (IOException e) {
				}
				
				updateMessage("laden fertig");
				return null;
			}
		};
		
		updateTask.messageProperty().addListener(new ChangeListener<String>() {
			@Override public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onlineResourcesFinished(newValue);
			}
		});
		
		if (cStart != null)
			cStart.prOnlineResources.setVisible(true);
		
		Thread thread = new Thread(updateTask, "updateOnlineResources");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	public void onlineResourcesFinished(String text) {
		switch (text) {
		case "neue Spieltypen":
			if (Dialog.okAbort("Neue Spieltypen sind verfügbar. Wollen Sie zum aktuellen wechseln?")) {
				cStart.cbGameTypes.getSelectionModel().selectLast();
			}
			break;
		case "neue Sprachen":
			// languages = ;
			Dialog.info("Neue Sprachen sind verfügbar");
			break;
		case "laden fertig":
			if (cStart != null) {
				cStart.prOnlineResources.setVisible(false);
			}
			break;
		}
	}
	
	
	public void loadOnlineAis() {
		
		Task<ObservableList<AiBase>> loadOnline = new Task<ObservableList<AiBase>>() {
			public ObservableList<AiBase> call() {
				ObservableList<AiBase> newOnline = MainApp.webConnector.getAis(MainApp.actualGameType.get());
				return newOnline;
			}
		};
		Task<ObservableList<AiBase>> loadOwn = new Task<ObservableList<AiBase>>() {
			public ObservableList<AiBase> call() {
				ObservableList<AiBase> newOwnOnline = null;
				if (MainApp.webConnector.isLoggedIn())		
					newOwnOnline = MainApp.webConnector.getOwnAis(MainApp.actualGameType.get());
				return newOwnOnline;
			}
		};

		loadOnline.valueProperty().addListener((observableValue, oldValue, newValue) -> {
				if (newValue != null){
					onlineAis.clear();
					onlineAis.addAll(newValue);
				}
		});
		loadOwn.valueProperty().addListener((observableValue, oldValue, newValue) -> {
				if (newValue != null){
					ownOnlineAis.clear();
					ownOnlineAis.addAll(newValue);
				}
		});

		Thread thread1 = new Thread(loadOnline, "loadOnlineAis");
		thread1.setDaemon(true);
		thread1.start();
		
		Thread thread2 = new Thread(loadOwn, "loadOwnOnlineAis");
		thread2.setDaemon(true);
		thread2.start();
	}
	
	
	public void showSplashStage(Stage splashStage) {
		
		final Task<Object> updateTask = new Task<Object>() {
			
			@Override protected Object call() throws InterruptedException {
				
				updateMessage("Gametypen werden geladen");
				try {
					webConnector.updateGametypes();
				} catch (NewException e) {
					gametypes = e.newValues;
				} catch (NothingDoneException | UpdateException e) {
				} catch (IOException e) {
					ErrorLog.write("Bitte stellen Sie beim ersten Start eine Verbindung zum Internet her");
					for (int i = 10; i >= 0; i--) {
						updateMessage("Keine Internetverbindung (" + i + ")");
						Thread.sleep(1000);
					}
					System.exit(1);
				}
				updateMessage("Sprachen werden geladen");
				
				try {
					webConnector.updateLanguages();
				} catch (NewException e) {
					languages = e.newValues;
				} catch (NothingDoneException e) {
				} catch (IOException e) {
					ErrorLog.write("Bitte stellen Sie beim ersten Start eine Verbindung zum Internet her");
					for (int i = 10; i >= 0; i--) {
						updateMessage("Keine Internetverbindung (" + i + ")");
						Thread.sleep(1000);
					}
					System.exit(1);
				}
				return null;
			}
		};
		
		// Screen erstellen
		ImageView img = new ImageView(Resources.codr());
		ProgressBar loadProgress = new ProgressBar();
		loadProgress.setPrefWidth(400 - 20);
		Label progressText = new Label("Die Spiellogiken werden heruntergeladen . . .");
		Pane splashLayout = new VBox();
		((VBox) splashLayout).setSpacing(5);
		((VBox) splashLayout).setAlignment(Pos.CENTER);
		splashLayout.getChildren().addAll(img, loadProgress, progressText);
		progressText.setAlignment(Pos.CENTER);
		splashLayout.setStyle("-fx-padding: 10; " + "-fx-border-color: derive(black, 90%); " + "-fx-border-width:1; " + "-fx-background-color: white;");
		progressText.textProperty().bind(updateTask.messageProperty());
		splashLayout.setEffect(new DropShadow());
		
		Scene splashScene = new Scene(splashLayout);
		splashStage.initStyle(StageStyle.UNDECORATED);
		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		splashStage.setScene(splashScene);
		splashStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - 400 / 2);
		splashStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - 200);
		splashStage.show();
		
		updateTask.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1), splashLayout);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0);
				fadeSplash.setOnFinished(actionEvent -> splashStage.hide());
				fadeSplash.play();
				
				showMainStage();
			}
		});
		
		Thread thread = new Thread(updateTask, "splashUpdate");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	public void showMainStage() {
		
		BorderPane root = new BorderPane();
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/RootLayout.fxml"));
			root = (BorderPane) loader.load();
			((ControllerRoot) loader.getController()).setMainApp(this);
		} catch (IOException e) {
			ErrorLog.write("RootLayout konnte nicht geladen werden (FXML-Fehler): " + e);
			e.printStackTrace();
		}
		
		settings.load(cStart);
		
		stage.setTitle("Codr");
		stage.getIcons().add(Resources.codrIcon());
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		
	}
	
}

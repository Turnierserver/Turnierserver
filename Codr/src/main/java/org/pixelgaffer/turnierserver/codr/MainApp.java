package org.pixelgaffer.turnierserver.codr;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

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

import org.apache.commons.io.FileUtils;
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


/**
 * Managet das Komplette Programm
 * 
 * @author Philip
 */
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
	public static GameManager gameManager = new GameManager();
	public static AiManager aiManager = new AiManager();
	
	public static StringProperty actualGameType = new SimpleStringProperty(null);
	public static ObservableList<String> gametypes = FXCollections.observableArrayList();
	public static ObservableList<String> languages = FXCollections.observableArrayList();
	
	public static ObservableList<GameOnline> onlineGames = FXCollections.observableArrayList();
	public static ObservableList<AiOnline> onlineAis = FXCollections.observableArrayList();
	public static ObservableList<AiOnline> ownOnlineAis = FXCollections.observableArrayList();
	
	
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
		
		checkNewVersion(false);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> exit()));
		
		
		stage = new Stage(StageStyle.DECORATED);
		
		settings = new Settings();
		settings.loadUrl();
		webConnector = new WebConnector("http://" + Settings.webUrl + "/api/");
		
		gametypes = webConnector.loadGametypesFromFile();
		languages = webConnector.loadLangsFromFile();
		
		CodeEditor.writeSyntax();
		
		if (gametypes == null || languages == null) {
			showSplashStage(_stage);
		} else {
			loadOnlineResources();
			showMainStage();
		}
		
		ErrorLog.write("Programm gestartet", true);
		
	}
	
	
	/**
	 * wird aufgerufen, wenn Codr geschlossen wird
	 */
	public void exit() {
		if (settings != null)
			settings.store(cStart);
		if (cAi != null && cAi.version != null)
			cAi.version.saveCode();
			
		if (cGame != null && cGame.runningGame != null) {
			try {
				cGame.runningGame.game.finishGame();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		checkNewVersion(true);
		
		ErrorLog.write("Programm beendet", true);
	}
	
	
	/**
	 * Überprüft in einem Thread, ob sich die Verbindung zum Server geändert hat.
	 * Wenn er fertig ist, wird die Benutzeroberfläche entsprechend angepasst.
	 */
	public static void updateConnected() {
		Task<Boolean> updateC = new Task<Boolean>() {
			public Boolean call() {
				if (MainApp.webConnector.ping()) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		cStart.prOnlineResources.setVisible(true);
		
		updateC.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			if (cSubmission == null)
				try {
					Thread.sleep(20);
				} catch (Exception e) {
				}
				
			cStart.prOnlineResources.setVisible(false);
			if (newValue) {
				cStart.lbIsOnline.setText("Es besteht eine Internetverbindung");
				cStart.btTryOnline.setText("nach Aktualisierungen suchen");
				cStart.vbLogin.setDisable(false);
				cGame.tabOnline.setDisable(false);
				cRoot.tabRanking.setDisable(false);
			} else {
				cStart.lbIsOnline.setText("Momentan besteht keine Internetverbindung");
				cStart.btTryOnline.setText("Erneut versuchen");
				cStart.vbLogin.setDisable(true);
				cGame.tabOnline.setDisable(true);
				cRoot.tabRanking.setDisable(true);
			}
		});
		
		Thread thread = new Thread(updateC, "updateConnected");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	/**
	 * Überprüft in einem Thread, ob der Benutzer eingeloggt ist.
	 * Wenn er fertig ist, wird die Benutzeroberfläche entsprechend angepasst.
	 */
	public static void updateLoggedIn() {
		
		Task<Boolean> updateL = new Task<Boolean>() {
			
			public Boolean call() {
				if (webConnector.isLoggedIn()) {
					return true;
				} else {
					return false;
				}
			}
		};
		
		cStart.prLogin.setVisible(true);
		cStart.prLogin1.setVisible(true);
		
		updateL.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			if (cSubmission == null)
				try {
					Thread.sleep(20);
				} catch (Exception e) {
				}
				
			if (newValue) {
				cStart.vbLogin.getChildren().clear();
				cStart.vbLogin.getChildren().add(cStart.hbLogout);
				cGame.tpNewGameOnline.setDisable(false);
				cGame.tpNewGameOnline.setExpanded(true);
				cAi.btUpload.setVisible(true);
				cRanking.btChallenge.setVisible(true);
			} else {
				cStart.vbLogin.getChildren().clear();
				cStart.vbLogin.getChildren().add(cStart.gpLogin);
				cGame.tpNewGameOnline.setDisable(true);
				cGame.tpNewGameOnline.setExpanded(false);
				cAi.btUpload.setVisible(false);
				cRanking.btChallenge.setVisible(false);
			}
			cStart.prLogin.setVisible(false);
			cStart.prLogin1.setVisible(false);
		});
		
		Thread thread = new Thread(updateL, "updateLoggedIn");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	/**
	 * Überprüft, ob eine neue Version von Codr im gleichen Verzeichnis existiert und updatet sich selbst.
	 * Dies funktioniert nur, wenn Codr als .jar kompiliert ist.
	 * 
	 * @param newStartWarning Gibt an, ob gewarnt werden soll, bevor Codr neu gestartet wird.
	 */
	public void checkNewVersion(boolean newStartWarning) {
		File myself = new File((System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0]));
		if (myself.isDirectory()) {
			ErrorLog.write("Du hast nicht die Jar-Version von Codr");
			return;
		}
		
		// ist neu
		if (myself.getName().equals("CodrNewVersion.jar")) {
			File oldCodr = new File("Codr.jar");
			
			for (int i = 0; i < 5; i++) { // 5 Versuche mit Pausen, um zu warten, bis oldCodr freigegeben ist.
				try {
					FileUtils.copyFile(myself, oldCodr);
					
					Runtime.getRuntime().exec(new String[] { "java", "-jar", oldCodr.getName() });
					System.exit(0);
					
				} catch (IOException e) {
					ErrorLog.write("Fehler beim Updaten: " + e);
				}
				try {
					Thread.sleep(100); // vor nächstem Versuch warten
				} catch (InterruptedException e) {
				}
			}
			
		} else { // ist normal
			File toDelete = new File("CodrNewVersion.jar");
			if (toDelete.exists()) {
				for (int i = 0; i < 5; i++) { // 5 Versuche mit Pausen, um zu warten, bis toDelete freigegeben ist.
					try {
						if (Resources.compareFiles(myself, toDelete)) {
							try {
								org.apache.commons.io.FileUtils.forceDelete(toDelete);
								ErrorLog.write("CodrNewVersion.jar wurde gelöscht, da sie identisch mit der aktuellen Version ist.");
								return;
							} catch (Exception e) {
								ErrorLog.write(e.toString());
							}
							
						} else {
							ErrorLog.write("Eine neue Version ist verfügbar, sie wird ausgeführt...");
							
							if (newStartWarning) {
								Dialog.info("Codr wird jetzt neu gestartet.");
							}
							
							Runtime.getRuntime().exec(new String[] { "java", "-jar", toDelete.getName() });
							ErrorLog.write("Ausführen fertig.");
							
							System.exit(0);
						}
					} catch (IOException e) {
						ErrorLog.write("Fehler beim Updaten: " + e);
						return;
					}
					
					try {
						Thread.sleep(100); // vor nächstem Versuch warten
					} catch (InterruptedException e) {
					}
				}
			}
		}
		
		
	}
	
	
	/**
	 * Sucht nach neunen Spieltypen, neuen Sprachen und Aktualisierungen von Codr.
	 */
	public void loadOnlineResources() {
		
		final Task<Object> updateTask = new Task<Object>() {
			
			@Override
			protected Object call() throws InterruptedException {
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
				
				try {
					byte[] onlineHash = webConnector.getCodrHash();
					byte[] myHash = Resources.getHash(new File((System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0])));

					if (!Arrays.equals(onlineHash, myHash))
						if (webConnector.updateCodr())
							updateMessage("neuer Codr");
							
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				updateMessage("laden fertig");
				return null;
			}
		};
		
		updateTask.messageProperty().addListener(new ChangeListener<String>() {
			
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				onlineResourcesFinished(newValue);
			}
		});
		
		if (cStart != null)
			cStart.prOnlineResources.setVisible(true);
			
		Thread thread = new Thread(updateTask, "updateOnlineResources");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	/**
	 * Nimmt die Ergebnisse der Suche von loadOnlineResources entgegen.
	 * @param text
	 */
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
		case "neuer Codr":
			if (Dialog.okAbort("Eine neue Version von Codr ist verfügbar.\nJetzt neustarten?")) {
				checkNewVersion(false);
			}
			
			break;
		case "laden fertig":
			if (cStart != null) {
				cStart.prOnlineResources.setVisible(false);
			}
			break;
		}
	}
	
	
	/**
	 * Lädt die online-KIs und -Spiele
	 */
	public void loadOnlineRanking() {
		
		Task<ObservableList<GameOnline>> loadOnlineGames = new Task<ObservableList<GameOnline>>() {
			
			public ObservableList<GameOnline> call() {
				ObservableList<GameOnline> newOnlineGames = MainApp.webConnector.getGames(MainApp.actualGameType.get());
				return newOnlineGames;
			}
		};
		Task<ObservableList<AiOnline>> loadOnline = new Task<ObservableList<AiOnline>>() {
			public ObservableList<AiOnline> call() {
				ObservableList<AiOnline> newOnline = null;
				try {
					newOnline = MainApp.webConnector.getAis(MainApp.actualGameType.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return newOnline;
			}
		};
		Task<ObservableList<AiOnline>> loadOwn = new Task<ObservableList<AiOnline>>() {
			
			public ObservableList<AiOnline> call() {
				ObservableList<AiOnline> newOwnOnline = null;
				if (MainApp.webConnector.isLoggedIn())
					newOwnOnline = MainApp.webConnector.getOwnAis(MainApp.actualGameType.get());
				return newOwnOnline;
			}
		};
		
		Thread thread1 = new Thread(loadOnlineGames, "loadOnlineGames");
		Thread thread2 = new Thread(loadOnline, "loadOnlineAis");
		Thread thread3 = new Thread(loadOwn, "loadOwnOnlineAis");
		
		loadOnlineGames.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue != null) {
				onlineGames.clear();
				onlineGames.addAll(newValue);
			}
		});
		loadOnline.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue != null) {
				onlineAis.clear();
				onlineAis.addAll(newValue);
			}
		});
		loadOwn.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue != null) {
				ownOnlineAis.clear();
				ownOnlineAis.addAll(newValue);
			}
		});
		
		thread1.setDaemon(true);
		thread1.start();
		
		thread2.setDaemon(true);
		thread2.start();
		
		thread3.setDaemon(true);
		thread3.start();
	}
	
	
//	/**
//	 * Verbindet die Spiele und die KIs miteinander, nachdem sie Runtergeladen wurden.
//	 */
//	public void connectGamesPlayers() {
//		Map<Integer, GameOnline> games = new HashMap<Integer, GameOnline>();
//		Map<Integer, AiOnline> ais = new HashMap<Integer, AiOnline>();
//		
//		for (GameOnline game : onlineGames) {
//			games.put(game.ID, game);
//		}
//		for (AiOnline ai : onlineAis) {
//			ais.put(ai.id, ai);
//		}
//		
//		
//		for (GameOnline game : onlineGames) {
//			for (ParticipantResult part : game.participants) {
//				part.ai = ais.get(part.aiID);
//			}
//		}
//		for (AiOnline ai : onlineAis) {
//			for (int id : ai.onlineGameIDs) {
//				ai.onlineGames.add(games.get(id));
//			}
//		}
//		return;
//	}
	
	
	/**
	 * Zeigt den Startbildschirm beim ersten Start von Codr an.
	 */
	public void showSplashStage(Stage splashStage) {
		
		final Task<Object> downloadTask = new Task<Object>() {
			
			@Override
			protected Object call() throws InterruptedException {
				
				updateMessage("Gametypen werden geladen");
				try {
					webConnector.updateGametypes();
				} catch (NewException e) {
					gametypes = e.newValues;
				} catch (NothingDoneException | UpdateException e) {
				} catch (IOException e) {
					ErrorLog.write("Bitte stellen Sie beim ersten Start eine Verbindung zum Internet her");
					for (int i = 100; i >= 0; i--) {
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
					for (int i = 100; i >= 0; i--) {
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
		progressText.textProperty().bind(downloadTask.messageProperty());
		splashLayout.setEffect(new DropShadow());
		
		Scene splashScene = new Scene(splashLayout);
		splashStage.initStyle(StageStyle.UNDECORATED);
		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		splashStage.setScene(splashScene);
		splashStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - 400 / 2);
		splashStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - 200);
		splashStage.setTitle("Codr");
		splashStage.getIcons().add(Resources.codrIcon());
		splashStage.show();
		
		downloadTask.messageProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue.equals("Keine Internetverbindung (100)")) {
				Dialog.info("Codr braucht beim ersten Start eine Internetverbindung.", "keine Internetverbindung");
				System.exit(1);
			}
		});
		
		downloadTask.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1), splashLayout);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0);
				fadeSplash.setOnFinished(actionEvent -> splashStage.hide());
				fadeSplash.play();
				
				showMainStage();
			}
		});
		
		Thread thread = new Thread(downloadTask, "splashUpdate");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	/**
	 * Kümmert sich um die Darstellung des Fensters.
	 */
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
		cStart.loadAis();
		
		settings.load(cStart);
		
		stage.setTitle("Codr");
		stage.getIcons().add(Resources.codrIcon());
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		
	}
	
}

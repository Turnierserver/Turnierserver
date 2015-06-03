package org.pixelgaffer.turnierserver.esu;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.util.List;

import javax.jws.Oneway;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringProperty;
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
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Exceptions.DeletedException;
import org.pixelgaffer.turnierserver.esu.utilities.Exceptions.NewException;
import org.pixelgaffer.turnierserver.esu.utilities.Exceptions.NothingDoneException;
import org.pixelgaffer.turnierserver.esu.utilities.Exceptions.UpdateException;
import org.pixelgaffer.turnierserver.esu.utilities.Dialog;
import org.pixelgaffer.turnierserver.esu.utilities.Resources;
import org.pixelgaffer.turnierserver.esu.utilities.WebConnector;
import org.pixelgaffer.turnierserver.esu.view.ControllerAiManagement;
import org.pixelgaffer.turnierserver.esu.view.ControllerGameManagement;
import org.pixelgaffer.turnierserver.esu.view.ControllerRanking;
import org.pixelgaffer.turnierserver.esu.view.ControllerRoot;
import org.pixelgaffer.turnierserver.esu.view.ControllerStartPage;
import org.pixelgaffer.turnierserver.esu.view.ControllerSubmission;
 
public class MainApp extends Application{
	
	
	public Stage stage;
	public ControllerRoot cRoot;
	public ControllerStartPage cStart;
	public ControllerAiManagement cAi;
	public ControllerGameManagement cGame;
	public ControllerRanking cRanking;
	public ControllerSubmission cSubmission;
	
	public WebConnector webConnector = new WebConnector("http://192.168.178.43:5000/api/", "192.168.178.43");//"http://thuermchen.com/api/");
	public GameManager gameManager = new GameManager();
	public AiManager aiManager = new AiManager();
	
	public static StringProperty actualGameType = new SimpleStringProperty(null);
	public static ObservableList<String> gametypes = FXCollections.observableArrayList();
	public static ObservableList<String> languages = FXCollections.observableArrayList();
	
	public boolean isOnline;
	
	/**
	 * Main-Methode
	 * 
	 * @param args Argumente
	 */
	public static void main(String[] args){
		launch(args);
	}
	
	/**
	 * start-Methode (wegen: extends Application)
	 */
	public void start(Stage _stage) throws Exception {
		ErrorLog.clear();
		ErrorLog.write("Programm startet...", true);
		
		stage = new Stage(StageStyle.DECORATED);

		
		gametypes = webConnector.loadGametypesFromFile();
		languages = webConnector.loadLangsFromFile();
		
		if (gametypes == null || languages == null){
			showSplashStage(_stage);
		}
		else{
			final Task updateTask = new Task() {
				@Override
				protected Object call() throws InterruptedException {
	
					updateMessage("Gametypen werden geladen");
					try {
						webConnector.updateGametypes();
					} catch (NewException e) {
						gametypes = e.newValues;
						if (Dialog.okAbort("Neue Spieltypen sind verfügbar. Wollen Sie zum aktuellen wechseln?")){
							cStart.cbGameTypes.getSelectionModel().selectLast();
						}
					} catch (UpdateException e) {
					}catch (NothingDoneException e) {}
	
					updateMessage("Sprachen werden geladen");
	
					try {
						webConnector.updateLanguages();
					} catch (NewException e) {
						languages = e.newValues;
						Dialog.info("Neue Sprachen sind verfügbar");
					} catch (NothingDoneException e) {}
					return null;
				}
			};
	
			new Thread(updateTask).start();
			
			showMainStage();
		}

		ErrorLog.write("Programm gestartet", true);

	}
	
	public void stop(){
		if (cAi.version != null)
			cAi.version.saveCode();
		ErrorLog.write("Programm beendet", true);
	}
	
	public void showSplashStage(Stage splashStage){

		final Task updateTask = new Task() {
			@Override
			protected Object call() throws InterruptedException {

				updateMessage("Gametypen werden geladen");
				try {
					webConnector.updateGametypes();
				} catch (NewException e) {
					gametypes = e.newValues;
				} catch (UpdateException e) {
				}catch (NothingDoneException e) {
					ErrorLog.write("Bitte stellen Sie beim ersten Start eine Verbindung zum Internet her");
					for (int i = 10; i >= 0; i--){
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
				} catch (UpdateException e) {
				} catch (NothingDoneException e) {
					ErrorLog.write("Bitte stellen Sie beim ersten Start eine Verbindung zum Internet her");
					for (int i = 10; i >= 0; i--){
						updateMessage("Keine Internetverbindung (" + i + ")");
						Thread.sleep(1000);
					}
					System.exit(1);
				}
				return null;
			}
		};
		
		
		
		//Screen erstellen
		ImageView img = new ImageView(Resources.defaultPicture());
		ProgressBar loadProgress = new ProgressBar();
		loadProgress.setPrefWidth(400 - 20);
		Label progressText = new Label("Die Spiellogiken werden heruntergeladen . . .");
		Pane splashLayout = new VBox();
		((VBox) splashLayout).setSpacing(5);
		((VBox) splashLayout).setAlignment(Pos.CENTER);
		splashLayout.getChildren().addAll(img, loadProgress, progressText);
		progressText.setAlignment(Pos.CENTER);
		splashLayout.setStyle(
			"-fx-padding: 10; " +
			"-fx-border-color: derive(black, 90%); " +
			"-fx-border-width:1; " +
			"-fx-background-color: white;"
		);
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
				fadeSplash.setToValue(0.5);
				fadeSplash.setOnFinished(actionEvent -> splashStage.hide());
				fadeSplash.play();
				
				showMainStage();
			}
		});
		
		new Thread(updateTask).start();
	}
	
	
	public void showMainStage(){
		
		BorderPane root = new BorderPane();
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/RootLayout.fxml"));
			root = (BorderPane) loader.load();
			((ControllerRoot) loader.getController()).setMainApp(this);
		} catch (IOException e) {
			ErrorLog.write("RootLayout konnte nicht geladen werden (FXML-Fehler)");
			e.printStackTrace();
		}
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
	}
	
}

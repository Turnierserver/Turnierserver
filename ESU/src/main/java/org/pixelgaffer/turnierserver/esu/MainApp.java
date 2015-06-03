package org.pixelgaffer.turnierserver.esu;

import java.nio.channels.Pipe;
import java.util.List;

import javax.jws.Oneway;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
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
		stage = _stage;

		ErrorLog.write("Programm startet...", true);

		gametypes = webConnector.loadGametypesFromFile();
		languages = webConnector.loadLangsFromFile();
		
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("view/RootLayout.fxml"));
		BorderPane root = (BorderPane) loader.load();
		((ControllerRoot) loader.getController()).setMainApp(this);
		
		ErrorLog.clear();
		ErrorLog.write("Programm gestartet", true);
		
		////////////Test//////////////////////////////////
		
		//////////////////////////////////////////////////
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		

	}
	
	public void stop(){
		if (cAi.version != null)
			cAi.version.saveCode();
		ErrorLog.write("Programm beendet", true);
	}
	
	
	
}

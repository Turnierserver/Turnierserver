package org.pixelgaffer.turnierserver.esu;

import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.view.ControllerGameManagement;
import org.pixelgaffer.turnierserver.esu.view.ControllerAiManagement;
import org.pixelgaffer.turnierserver.esu.view.ControllerRanking;
import org.pixelgaffer.turnierserver.esu.view.ControllerRoot;
import org.pixelgaffer.turnierserver.esu.view.ControllerStartPage;
import org.pixelgaffer.turnierserver.esu.view.ControllerSubmission;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
 
public class MainApp extends Application{
	
	
	public Stage stage;
	public ControllerRoot cRoot;
	public ControllerStartPage cStart;
	public ControllerAiManagement cAi;
	public ControllerGameManagement cGame;
	public ControllerRanking cRanking;
	public ControllerSubmission cSubmission;
	
	
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
		cAi.version.saveCode();
		ErrorLog.write("\n");
		ErrorLog.write("Programm beendet", true);
	}
	
	
}

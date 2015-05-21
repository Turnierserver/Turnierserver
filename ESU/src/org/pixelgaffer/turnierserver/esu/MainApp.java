package org.pixelgaffer.turnierserver.esu;
 
import org.pixelgaffer.turnierserver.esu.Player.NewVersionType;
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
	
	public PlayerManager manager = new PlayerManager();
	
	public Stage stage;
	public ControllerRoot cRoot;
	public ControllerStartPage cStart;
	public ControllerAiManagement cAi;
	public ControllerGameManagement cGame;
	public ControllerRanking cRanking;
	public ControllerSubmission cSubmission;
	
	public static enum Language{
		Java, Phyton
	}
	
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
		ErrorLog.write("", true);
		
		
		////////////Test//////////////////////////////////
		
		manager.loadPlayers();
		cAi.showPlayers(manager);
		cAi.showPlayer(manager.players.get(0), null);
//		Player pp = new Player("testAI");
//		ErrorLog.write("Titel:" + pp.title);
//		ErrorLog.write("Beschreibung: " + pp.description);
//		ErrorLog.write("Version 0: " + pp.versions.get(0).compiled);
//		//pp.setPicture(pp.getPicture());
//		cAi.image.setImage(pp.getPicture());
		
		//////////////////////////////////////////////////
		
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	
}

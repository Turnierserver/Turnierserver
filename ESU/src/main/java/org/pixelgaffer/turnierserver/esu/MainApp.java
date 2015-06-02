package org.pixelgaffer.turnierserver.esu;

import java.nio.channels.Pipe;
import java.util.List;

import javax.jws.Oneway;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
	
	public List<String> gametypes;
	public List<String> langs;
	
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
		
		gametypes = webConnector.getGametypes();
		langs = webConnector.getLanguages();
	}
	
	public void stop(){
		cAi.version.saveCode();
		ErrorLog.write("\n");
		ErrorLog.write("Programm beendet", true);
	}
	
	public boolean checkOnline() {
		if(webConnector.ping()) {
			gametypes = webConnector.getGametypes();
			langs = webConnector.getGametypes();
			if(gametypes != null && langs != null) {
				return true;
			}
		}
		return false;
	}
	
	
}

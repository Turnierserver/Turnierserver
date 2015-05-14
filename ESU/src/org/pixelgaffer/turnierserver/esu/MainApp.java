package org.pixelgaffer.turnierserver.esu;
 
import org.pixelgaffer.turnierserver.esu.view.ControllerGameManagement;
import org.pixelgaffer.turnierserver.esu.view.ControllerKiManagement;
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
	
	public ControllerRoot cRoot;
	public ControllerStartPage cStart;
	public ControllerKiManagement cKi;
	public ControllerGameManagement cGame;
	public ControllerRanking cRanking;
	public ControllerSubmission cSubmission;
	
	enum Language{
		Java, Phyton
	}
	
	
	public static void main(String[] args){
		ErrorLog.clear();
		ErrorLog.write("", true);
		new Player("test3");
		launch(args);
	}
	 
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("view/RootLayout.fxml"));
		BorderPane root = (BorderPane) loader.load();
		((ControllerRoot) loader.getController()).setMainApp(this);
		
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setHeight(700);
		stage.setWidth(1100);
		stage.show();
	}
	
	
}

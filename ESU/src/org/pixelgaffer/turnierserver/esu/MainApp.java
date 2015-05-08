package org.pixelgaffer.turnierserver.esu;
 
import org.pixelgaffer.turnierserver.esu.view.ControllerStartPage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
 
public class MainApp extends Application{
 
	ControllerStartPage control;
	
	
	public static void main(String[] args){
		launch(args);
	}
	 
	public void start(Stage stage) throws Exception {
		BorderPane root = (BorderPane) FXMLLoader.load(getClass().getResource("view/RootLayout.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setHeight(700);
		stage.setWidth(1100);
		stage.show();
	}
	
	
}

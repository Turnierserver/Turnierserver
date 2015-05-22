package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Dialog {
	
	
	private static boolean generalDialog(String text, String title, AlertType type){
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
		    return false;
		}
	}
	
	public static boolean info(String text){
		return info(text, "Information");
	}
	public static boolean info(String text, String title){
		return generalDialog(text, title, AlertType.INFORMATION);
	}
	
	public static boolean warning(String text){
		return warning(text, "Warnung");
	}
	public static boolean warning(String text, String title){
		return generalDialog(text, title, AlertType.WARNING);
	}

	public static boolean error(String text){
		return error(text, "Fehler");
	}
	public static boolean error(String text, String title){
		return generalDialog(text, title, AlertType.ERROR);
	}
	
	public static boolean okAbort(String text){
		return okAbort(text, "Bitte bestätigen");
	}
	public static boolean okAbort(String text, String title){
		return generalDialog(text, title, AlertType.CONFIRMATION);
	}
	
	public static String textInput(String text){
		return textInput(text, "Bitte Text eingeben");
	}
	public static String textInput(String text, String title){
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Text Input Dialog");
		dialog.setContentText("Please enter your name:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    return result.get();
		}
		return null;
	}
	
	public static File fileChooser(Stage stage){
		return fileChooser(stage, "Bitte eine Datei auswählen");
	}
	public static File fileChooser(Stage stage, String title){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		return fileChooser.showOpenDialog(stage);
	}

	public static File folderChooser(Stage stage){
		return folderChooser(stage, "Bitte einen Ordner auswählen");
	}
	public static File folderChooser(Stage stage, String title){
		DirectoryChooser folderChooser = new DirectoryChooser();
		folderChooser.setTitle(title);
		return folderChooser.showDialog(stage);
	}
	
}

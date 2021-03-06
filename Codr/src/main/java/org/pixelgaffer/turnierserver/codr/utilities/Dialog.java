package org.pixelgaffer.turnierserver.codr.utilities;


import java.io.File;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.pixelgaffer.turnierserver.codr.AiBase;
import org.pixelgaffer.turnierserver.codr.AiFake;
import org.pixelgaffer.turnierserver.codr.MainApp;


/**
 * Diese Klasse stellt einige Dialog-Fenster bereit, die unkompliziert genutzt werden können
 * 
 * @author Philip
 */
public class Dialog {
	
	/**
	 * Ruft einen javafx.scene.control.Alert auf
	 * 
	 * @param text Text, der dargestellt werden soll
	 * @param title Titel der Nachricht
	 * @param type Typ des Dialogfensters
	 * @return true, wenn auf ok geklickt wurde
	 */
	private static boolean generalDialog(String text, String title, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(text);
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Ruft einen Info-Dialog auf
	 * 
	 * @param text Text, der dargestellt werden soll
	 * @return true, wenn auf ok geklickt wurde
	 */
	public static boolean info(String text) {
		return info(text, "Information");
	}
	
	
	public static boolean info(String text, String title) {
		return generalDialog(text, title, AlertType.INFORMATION);
	}
	
	
	/**
	 * Ruft einen Warnungs-Dialog auf
	 * 
	 * @param text Text, der dargestellt werden soll
	 * @return true, wenn auf ok geklickt wurde
	 */
	public static boolean warning(String text) {
		return warning(text, "Warnung");
	}
	
	
	public static boolean warning(String text, String title) {
		return generalDialog(text, title, AlertType.WARNING);
	}
	
	
	/**
	 * Ruft einen Fehler-Dialog auf
	 * 
	 * @param text Text, der dargestellt werden soll
	 * @return true, wenn auf ok geklickt wurde
	 */
	public static boolean error(String text) {
		return error(text, "Fehler");
	}
	
	
	public static boolean error(String text, String title) {
		ErrorLog.write("Dialog.ERROR: " + text);
		return generalDialog(text, title, AlertType.ERROR);
	}
	
	
	/**
	 * Ruft einen Ok/Abbruch-Dialog auf
	 * 
	 * @param text Text, der dargestellt werden soll
	 * @return true, wenn auf ok geklickt wurde
	 */
	public static boolean okAbort(String text) {
		return okAbort(text, "Bitte bestätigen");
	}
	
	
	public static boolean okAbort(String text, String title) {
		return generalDialog(text, title, AlertType.CONFIRMATION);
	}
	
	
	/**
	 * Ruft einen Text-Eingabe-Dialog auf
	 * 
	 * @param text Text, der dargestellt werden soll
	 * @return den eingegebenen Text
	 */
	public static String textInput(String text) {
		return textInput(text, "Bitte Text eingeben", "");
	}
	
	
	public static String textInput(String text, String title, String defaultText) {
		TextInputDialog dialog = new TextInputDialog(defaultText);
		dialog.setTitle(title);
		dialog.setHeaderText(null);
		dialog.setContentText(text);
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}
	
	
	public static String textInput(String text, String title) {
		return textInput(text, title, "");
	}
	
	
	/**
	 * Ruft einen Dateiauswahl-Dialog auf
	 * 
	 * @param --> mainApp.stage; Stage, auf der der Dialog dargestellt werden soll
	 * @return die ausgewählte Datei
	 */
	public static File fileChooser(Stage stage) {
		return fileChooser(stage, "Bitte eine Datei auswählen");
	}
	
	
	public static File fileChooser(Stage stage, String title) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		return fileChooser.showOpenDialog(stage);
	}
	
	
	/**
	 * Ruft einen Datei-Speicher-Dialog auf
	 * 
	 * @param --> mainApp.stage; Stage, auf der der Dialog dargestellt werden soll
	 * @return die ausgewählte Datei
	 */
	public static File fileSaver(Stage stage) {
		return fileChooser(stage, "Bitte eine Datei auswählen");
	}
	
	
	public static File fileSaver(Stage stage, String title, String defaultPath) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(new File(defaultPath));
		return fileChooser.showSaveDialog(stage);
	}
	
	
	/**
	 * Ruft einen Ordnerauswahl-Dialog auf
	 * 
	 * @param --> mainApp.stage; Stage, auf der der Dialog dargestellt werden soll
	 * @return den ausgewählten Ordner
	 */
	public static File folderChooser(Stage stage) {
		return folderChooser(stage, "Bitte einen Ordner auswählen");
	}
	
	
	public static File folderChooser(Stage stage, String title) {
		DirectoryChooser folderChooser = new DirectoryChooser();
		folderChooser.setTitle(title);
		return folderChooser.showDialog(stage);
	}
	
	
	/**
	 * Öffnet einen Dialog, in dem der Spieler auswählen kann,
	 * zu welcher KI die hochzuladende Version hinzugefügt werden soll.
	 * 
	 * @return
	 */
	public static AiBase selectOwnVersion() {
		
		if (MainApp.ownOnlineAis == null)
			return null;
			
		ObservableList<AiBase> list = FXCollections.observableArrayList();
		list.addAll(MainApp.ownOnlineAis);
		list.add(new AiFake());
		
		ChoiceDialog<AiBase> dialog = new ChoiceDialog<>();
		dialog.getItems().addAll(list);
		dialog.setSelectedItem(list.get(0));
		dialog.setTitle("Version hochladen");
		dialog.setHeaderText("Wähle bitte eine KI aus, zu dem die Version hochgeladen werden soll.");
		dialog.setContentText("KI:");
		
		Optional<AiBase> result = dialog.showAndWait();
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}
}

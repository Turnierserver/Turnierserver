package org.pixelgaffer.turnierserver.esu;

import java.io.*;

import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

public class CodeEditor {
	
	private StringProperty text = new SimpleStringProperty("");
	private String savedText = "";
	private File document;
	public boolean loaded = false;
	
	/**
	 * Initialisiert den CodeEditor mit der zu zeigenden Datei
	 * 
	 * @param doc
	 */
	public CodeEditor(File doc){
		document = doc;
	}
	
	/**
	 * Überprüft, ob der angezeigte Text mit seiner gespeicherten Datei übereinstimmt
	 * 
	 * @return true, wenn savedText != text
	 */
	public boolean hasChanged(){
		if (loaded)
			return !savedText.equals(text.get());
		else
			return false;
	}
	
	/**
	 * Erstellt ein Tab, das in die Tab-Leiste eingefügt werden kann
	 * 
	 * @return das erstellte Tab
	 */
	public Tab getView(){
		load();
		
		//WebView codeView = new WebView();
		//codeView.getEngine().loadContent(text);
		
		TextArea codeView = new TextArea();
		codeView.textProperty().bindBidirectional(text);
		
		BorderPane pane = new BorderPane(codeView);
		return new Tab(document.getName(), pane);
	}
	
	
	/**
	 * Lädt den Inhalt der Datei in die StringProperty text
	 */
	public void load(){
		try {
			FileReader fileReader = new FileReader(document);
			BufferedReader reader = new BufferedReader(fileReader);
			
			String zeile = reader.readLine();
			if (zeile != null)
				text.set(zeile);
			zeile = reader.readLine();
			while (zeile != null) {
				text.set(text.get() + "\n" + zeile);
				zeile = reader.readLine();
			}
			reader.close();
			
			savedText = text.get();
			loaded = true;
		}
		catch (FileNotFoundException e) {
			ErrorLog.write("Quellcode konnte nicht gefunden werden");
		} catch (IOException e) {
			ErrorLog.write("Quellcode konnte nicht gelesen werden");
		}
	}
	
	/**
	 * Speichert den Inhalt der StringProperty text in die Datei
	 */
	public void save(){
		if (!hasChanged())
			return;
		forceSave();
	}
	
	/**
	 * Speichert den Inhalt der StringProperty text in eine Datei.
	 * Dabei wird nicht überprüft, ob sich der Inhalt verändert hat.
	 */
	public void forceSave(){
		try {
			FileWriter writer = new FileWriter(document, false);
			writer.write(text.get());
			writer.close();
			savedText = text.get();
		} catch (IOException e) {
			ErrorLog.write("Quellcode konnte nicht bearbeitet werden");
		}
	}
	
}

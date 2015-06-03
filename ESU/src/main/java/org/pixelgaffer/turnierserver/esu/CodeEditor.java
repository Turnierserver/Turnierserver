package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.w3c.dom.Document;

public class CodeEditor {

	private String savedText = "";
	private File document;
	public boolean loaded = false;
	private WebView codeView;

	/**
	 * Initialisiert den CodeEditor mit der zu zeigenden Datei
	 * 
	 * @param doc
	 */
	public CodeEditor(File doc) {
		document = doc;
		codeView = new WebView();
		try {
			codeView.getEngine().loadContent(IOUtils.toString(getClass().getResourceAsStream("view/editor.html"), "UTF-8").replace("${file}", doc.getAbsolutePath()).replace("${theme}", "eclipse"));
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		codeView.getEngine().documentProperty().addListener(new ChangeListener<Document>() {
			public void changed(ObservableValue<? extends Document> prop, Document oldDoc, Document newDoc) {
				codeView.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
				load();
			}
		});
	}

	/**
	 * überprüft, ob der angezeigte Text mit seiner gespeicherten Datei
	 * übereinstimmt
	 * 
	 * @return true, wenn savedText != text
	 */
	public boolean hasChanged() {
		if (loaded)
			return !savedText.equals(getCode());
		else
			return false;
	}

	public String getCode() {
		return (String) codeView.getEngine().executeScript("editor.getValue();");
	}

	public void setCode(String tanga) {
		codeView.getEngine().executeScript("editor.setValue(\"" + StringEscapeUtils.escapeJavaScript(tanga) + "\");");
	}

	/**
	 * Erstellt ein Tab, das in die Tab-Leiste eingefügt werden kann
	 * 
	 * @return das erstellte Tab
	 */
	public Tab getView() {
		BorderPane pane = new BorderPane(codeView);
		return new Tab(document.getName(), pane);
	}

	/**
	 * Lädt den Inhalt der Datei in die StringProperty text
	 */
	public void load() {
		try {
			setCode(FileUtils.readFileToString(document));

			savedText = getCode();
			loaded = true;
		} catch (FileNotFoundException e) {
			ErrorLog.write("Quellcode konnte nicht gefunden werden");
		} catch (IOException e) {
			ErrorLog.write("Quellcode konnte nicht gelesen werden");
		}
	}

	/**
	 * Speichert den Inhalt der StringProperty text in die Datei
	 */
	public void save() {
		if (!hasChanged())
			return;
		forceSave();
	}

	/**
	 * Speichert den Inhalt der StringProperty text in eine Datei. Dabei wird
	 * nicht �berpr�ft, ob sich der Inhalt ver�ndert hat.
	 */
	public void forceSave() {
		try {
			FileWriter writer = new FileWriter(document, false);
			writer.write(getCode());
			writer.close();
			savedText = getCode();
		} catch (IOException e) {
			ErrorLog.write("Quellcode konnte nicht bearbeitet werden");
		}
	}

}

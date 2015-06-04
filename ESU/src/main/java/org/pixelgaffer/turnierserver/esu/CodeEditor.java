package org.pixelgaffer.turnierserver.esu;

import java.awt.TextField;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Paths;
import org.w3c.dom.Document;

public class CodeEditor {

	private TextArea ta; // TODO: nur Übergangslösung --> löschen

	private String savedText = "";
	private File document;
	public boolean loaded = false;
	private WebView codeView;

	public static void writeAce() {
		File aceFolder = new File(Paths.aceFolder());
		if (aceFolder.exists()) {
			return;
		}
		try {
			aceFolder.mkdirs();
			FileUtils.write(new File(aceFolder, "bundle.js"), IOUtils
					.toString(CodeEditor.class.getResource("view/bundle.js")));
			FileUtils
					.write(new File(aceFolder, "editor.html"), IOUtils
							.toString(CodeEditor.class
									.getResource("view/editor.html")));
		} catch (IOException e) {
			ErrorLog.write("Konnte Ace nicht schreiben!");
		}
	}

	/**
	 * Initialisiert den CodeEditor mit der zu zeigenden Datei
	 * 
	 * @param doc
	 */
	public CodeEditor(File doc) {
		document = doc;
		codeView = new WebView();
		try {
			codeView.getEngine().load(
					new File(Paths.aceFolder() + "/editor.html").toURI()
							.toURL().toString());
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		codeView.getEngine().setOnAlert(new EventHandler<WebEvent<String>>() {
			@Override
			public void handle(WebEvent<String> event) {
				System.err.println("This is an alert: " + event);
			}
		});

		codeView.getEngine().documentProperty()
				.addListener(new ChangeListener<Document>() {
					public void changed(
							ObservableValue<? extends Document> prop,
							Document oldDoc, Document newDoc) {
						codeView.getEngine().executeScript(
								"editor.setTheme(\"ace/theme/eclipse\");");
						codeView.getEngine().executeScript(
								"editor.getSession().setMode(modelist.getModeForPath(\""
										+ doc.getName() + "\").mode);");
						// codeView.getEngine().executeScript("if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
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
		return (String) codeView.getEngine()
				.executeScript("editor.getValue();");
	}

	public void setCode(String t) {
//		codeView.getEngine().executeScript("editor.setValue('')");
//		new Thread(new Task() {
//			public Object call() {
//				for (int i = 0; i < t.length(); i++) {
//					char c = t.charAt(i);
//					String s = StringEscapeUtils.escapeJavaScript(c + "");
//					System.out.print(s);
//					codeView.getEngine().executeScript(
//							"editor.setValue(editor.getValue() + \"" + s
//									+ "\",1);");
//				}
//				return null;
//			}
//		}).start();
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

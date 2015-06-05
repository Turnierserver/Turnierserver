package org.pixelgaffer.turnierserver.codr.utilities;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;

import org.pixelgaffer.turnierserver.codr.CodrAi.AiMode;
import org.pixelgaffer.turnierserver.codr.view.ControllerStartPage;



public class Settings {
	
	ControllerStartPage cStart;
	
	public static String webUrl = "localhost:5000";
	
	
	public Settings(ControllerStartPage c) {
		cStart = c;
		load();
	}
	
	
	public void store() {
		Properties prop = new Properties();
		
		prop.setProperty("webUrl", webUrl);
		try {
			prop.setProperty("theme", cStart.btTheme.isSelected() + "");
			prop.setProperty("fontSize", cStart.slFontSize.getValue() + "");
			prop.setProperty("scrollSpeed", cStart.slScrollSpeed.getValue() + "");
			prop.setProperty("pythonInterpreter", cStart.tbPythonInterpreter.getText());
			prop.setProperty("cplusplusCompiler", cStart.tbCplusplusCompiler.getText());
		} catch (NullPointerException e1) {
		}
		
		try {
			Writer writer = new FileWriter(Paths.settings());
			prop.store(writer, "Settings");
			writer.close();
		} catch (IOException e) {
			ErrorLog.write("Es kann keine Settings-Datei angelegt werden.");
		}
	}
	
	
	public void load() {
		try {
			Reader reader = new FileReader(Paths.settings());
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			
			String newUrl = prop.getProperty("webUrl");
			if (newUrl != null) {
				webUrl = newUrl;
			}
			
			cStart.btTheme.setSelected(Boolean.parseBoolean(prop.getProperty("theme")));
			cStart.slFontSize.setValue(Double.parseDouble(prop.getProperty("fontSize")));
			cStart.slScrollSpeed.setValue(Double.parseDouble(prop.getProperty("scrollSpeed")));
			cStart.tbPythonInterpreter.setText(prop.getProperty("pythonInterpreter"));
			cStart.tbCplusplusCompiler.setText(prop.getProperty("cplusplusCompiler"));
		} catch (IOException e) {
			ErrorLog.write("Fehler bei Laden aus der settings.txt");
		} catch (NullPointerException e) {
		}
	}
	
	
	
}

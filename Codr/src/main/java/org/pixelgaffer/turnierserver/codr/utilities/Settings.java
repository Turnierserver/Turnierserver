package org.pixelgaffer.turnierserver.codr.utilities;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import org.pixelgaffer.turnierserver.codr.view.ControllerStartPage;


/**
 * übernimmt die Speicherung von einigen Einstellungen in die settings.prop
 * 
 * @author Philip
 */
public class Settings {
	
	public static String webUrl = "thuermchen.com";
	
	
	public void store(ControllerStartPage cStart) {
		Properties prop = new Properties();
		prop.setProperty("webUrl", webUrl);
		
		if (cStart == null) {
			System.out.println("Konnte Einstellungen nicht speichern");
		} else {
			prop.setProperty("theme", cStart.btTheme.isSelected() + "");
			prop.setProperty("fontSize", cStart.slFontSize.getValue() + "");
			prop.setProperty("pythonInterpreter", cStart.tbPythonInterpreter.getText());
			prop.setProperty("cplusplusCompiler", cStart.tbCplusplusCompiler.getText());
			prop.setProperty("cplusplusCompilerType", cStart.cbCplusplusCompilerType.getValue());
			prop.setProperty("jdkHome", cStart.tbJDK.getText());
			prop.setProperty("email", cStart.tbEmail.getText());
		}
		
		try {
			Writer writer = new FileWriter(Paths.settings());
			prop.store(writer, "Settings");
			writer.close();
		} catch (IOException e) {
			ErrorLog.write("Es kann keine Settings-Datei angelegt werden.");
			return;
		}
	}
	
	
	public void loadUrl() {
		Properties prop = new Properties();
		
		try {
			Reader reader = new FileReader(Paths.settings());
			prop.load(reader);
			reader.close();
			
		} catch (IOException e) {
			ErrorLog.write("Fehler bei Laden aus der settings.txt");
			return;
		}
		
		String newUrl = prop.getProperty("webUrl");
		if (newUrl != null) {
			webUrl = newUrl;
		}
	}
	
	
	public void load(ControllerStartPage cStart) {
		Properties prop = new Properties();
		
		try {
			Reader reader = new FileReader(Paths.settings());
			prop.load(reader);
			reader.close();
		} catch (IOException e) {
			ErrorLog.write("Fehler bei Laden aus der settings.txt");
			return;
		}
		String newUrl = prop.getProperty("webUrl");
		if (newUrl != null) {
			webUrl = newUrl;
		}
		
		if (cStart == null) {
			System.out.println("Konnte Einstellungen nicht laden  (Fatal ERROR)");
		} else {
			try {
				cStart.btTheme.setSelected(Boolean.parseBoolean(prop.getProperty("theme")));
				cStart.slFontSize.setValue(Double.parseDouble(prop.getProperty("fontSize")));
				cStart.tbPythonInterpreter.setText(prop.getProperty("pythonInterpreter"));
				cStart.tbCplusplusCompiler.setText(prop.getProperty("cplusplusCompiler"));
				if (prop.getProperty("cplusplusCompilerType") != null)
					cStart.cbCplusplusCompilerType.getSelectionModel().select(prop.getProperty("cplusplusCompilerType"));
				cStart.tbJDK.setText(prop.getProperty("jdkHome"));
				cStart.tbEmail.setText(prop.getProperty("email"));
				
			} catch (NullPointerException e) {
				System.out.println("Konnte Einstellungen nicht laden (Dies ist beim ersten Start normal)");
			}
		}
	}
	
}

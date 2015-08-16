package org.pixelgaffer.turnierserver.codr;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;


/**
 * Managet das Laden der
 * - in Codr gespeicherten KIs
 * - extern gespeicherten KIs
 * - SimplePlayer-KIs
 * 
 * @author Philip
 */
public class AiManager {
	
	
	public ObservableList<AiSimple> ais = FXCollections.observableArrayList();
	
	
	/**
	 * Lädt alle Spieler aus dem Dateisystem in die Liste
	 */
	public void loadAis() {
		
		Task<AiExtern> load = new Task<AiExtern>() {
			
			public AiExtern call() {
				// in Codr gespeicherte KIs
				ais.clear();
				File dir = new File(Paths.aiFolder());
				dir.mkdirs();
				File[] playerDirs = dir.listFiles();
				if (playerDirs == null) {
					ErrorLog.write("keine Spieler vorhanden");
				} else {
					for (int i = 0; i < playerDirs.length; i++) {
						if (playerDirs[i].isDirectory())
							ais.add(new AiSaved(playerDirs[i].getName()));
					}
				}
				
				// extern gespeicherte KIs
				File externDir = new File(Paths.aiExternFolder());
				externDir.mkdirs();
				File[] externDirs = externDir.listFiles();
				if (externDirs == null) {
					ErrorLog.write("keine externen Spieler vorhanden");
				} else {
					for (int i = 0; i < externDirs.length; i++) {
						if (externDirs[i].isDirectory()) {
							AiExtern newAi = new AiExtern(externDirs[i].getName());
							if (!new File(newAi.path).exists()) {
								updateValue(newAi);
							} else {
								ais.add(newAi);
							}
						}
					}
				}
				
				
				// simplePlayer, die aus dem Download-Ordner geladen werden
				File simpleDir = new File(Paths.simplePlayerFolder(MainApp.actualGameType.get()));
				simpleDir.mkdirs();
				File[] simpleDirs = simpleDir.listFiles();
				if (simpleDirs == null) {
					ErrorLog.write("keine SimplePlayer vorhanden");
				} else {
					for (int i = 0; i < simpleDirs.length; i++) {
						if (simpleDirs[i].isDirectory())
							ais.add(new AiSimple(simpleDirs[i].getName()));
					}
				}
				
				return null;
			}
		};
		
		
		load.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue == null)
				return;
				
			if (Dialog.okAbort("Der Pfad der KI " + newValue.title + " existiert nicht.\nWollen Sie die KI behalten?\nWenn Sie auf 'Abbrechen' klicken, wird die KI verworfen.")) {
				File result = Dialog.folderChooser(MainApp.stage, "Wählen Sie einen neuen Ordner aus.");
				if (result != null) {
					newValue.path = result.getPath();
					newValue.storeProps();
					ais.add(newValue);
				} else {
					try {
						FileUtils.deleteDirectory(new File(Paths.ai(newValue)));
					} catch (IOException e) {
					}
				}
			} else {
				try {
					FileUtils.deleteDirectory(new File(Paths.ai(newValue)));
				} catch (IOException e) {
				}
			}
		});
		
		
		Thread thread = new Thread(load, "updateLoggedIn");
		thread.setDaemon(true);
		thread.start();
		
	}
	
	
}

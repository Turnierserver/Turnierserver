package org.pixelgaffer.turnierserver.codr.view;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;

import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;



public class TreeFileCell extends TreeCell<File> {
	
	private TextField tbEdit;
	private ContextMenu fileMenu = new ContextMenu();
	private ContextMenu folderMenu = new ContextMenu();
	
	
	public TreeFileCell() {
		
		MenuItem addFileItem = new MenuItem("Neue Datei");
		folderMenu.getItems().add(addFileItem);
		addFileItem.setOnAction(new EventHandler() {
			public void handle(Event t) {
				addFile();
			}
		});
		
		MenuItem addFolderItem = new MenuItem("Neuer Ordner");
		folderMenu.getItems().add(addFolderItem);
		addFolderItem.setOnAction(new EventHandler() {
			public void handle(Event t) {
				addFolder();
			}
		});
		
		MenuItem renameItem = new MenuItem("Umbenennen");
		folderMenu.getItems().add(renameItem);
		renameItem.setOnAction(new EventHandler() {
			public void handle(Event t) {
				rename();
			}
		});
		
		MenuItem deleteItem = new MenuItem("Löschen");
		folderMenu.getItems().add(deleteItem);
		deleteItem.setOnAction(new EventHandler() {
			public void handle(Event t) {
				delete();
			}
		});
		
		
		MenuItem renameItem2 = new MenuItem("Umbenennen");
		fileMenu.getItems().add(renameItem2);
		renameItem2.setOnAction(new EventHandler() {
			public void handle(Event t) {
				rename();
			}
		});
		
		MenuItem deleteItem2 = new MenuItem("Löschen");
		fileMenu.getItems().add(deleteItem2);
		deleteItem2.setOnAction(new EventHandler() {
			public void handle(Event t) {
				delete();
			}
		});
	}
	
	
	
	public void addFile() {
		String result = Dialog.textInput("Bitte den Dateinamen eingeben (mit Endung)", "Neue Datei");
		
		File file = new File(getItem().getPath() + "/" + result);
		if (file.exists()) {
			Dialog.error("Diese Datei existiert schon.");
			return;
		}
		
		try {
			FileWriter writer = new FileWriter(file, false);
			writer.write("");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			ErrorLog.write("Fehler beim anlegen einer neuen Datei: " + e);
		}
		commitEdit(new File(""));
		setGraphic(new TextField("Hallo"));
	}
	
	
	public void addFolder() {
		String result = Dialog.textInput("Bitte den Namen eingeben", "Neuer Ordner");
		
		File dir = new File(getItem().getPath() + "/" + result);
		if (dir.exists()) {
			Dialog.error("Dieser Ordner existiert schon.");
			return;
		}
		
		dir.mkdir();
		commitEdit(new File(""));
		setGraphic(new TextField("Hallo"));
	}
	
	
	public void rename() {
		String result = Dialog.textInput("Bitte den neuen Dateinamen eingeben", "Umbenennen", getItem().getName());
		
		File file = new File(getItem().getParent() + "/" + result);
		if (file.exists()) {
			Dialog.error("Diese Datei existiert schon.");
			return;
		}
		
		if (result != null) {
			getItem().renameTo(file);
		}
		commitEdit(new File(""));
		setGraphic(new TextField("Hallo"));
	}
	
	
	public void delete() {
		String elementText = "die Datei";
		if (getItem().isDirectory())
			elementText = "den Ordner";
		if (Dialog.okAbort("Wollen Sie " + elementText + " wirklich löschen?", "Löschen")) {
			if (getItem().isDirectory()) {
				try {
					FileUtils.deleteDirectory(getItem());
				} catch (IOException e) {
					Dialog.error("Fehler beim löschen des Ordners");
				}
			} else {
				getItem().delete();
			}
		}
		commitEdit(new File(""));
		setGraphic(new TextField("Hallo"));
	}
	
	
	
	@Override public void updateItem(File item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (tbEdit != null) {
					tbEdit.setText(getItem().getName());
				}
				setText(null);
				setGraphic(tbEdit);
			} else {
				setText(getItem().getName());
				setGraphic(getTreeItem().getGraphic());
			}
			
			if (getItem().isDirectory()) {
				setContextMenu(folderMenu);
			} else {
				setContextMenu(fileMenu);
			}
		}
	}
	
	
	
	private String getString() {
		return getItem() == null ? "" : getItem().getName();
	}
	
}

/*
 * TreeFileCell.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pixelgaffer.turnierserver.codr.view;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;

import org.apache.commons.io.FileUtils;
import org.pixelgaffer.turnierserver.codr.utilities.Dialog;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;



public class TreeFileCell extends TreeCell<File> {
	
	private TextField tbEdit;
	private ContextMenu fileMenu = new ContextMenu();
	private ContextMenu folderMenu = new ContextMenu();
	
	private MenuItem addFileItem = new MenuItem("Neue Datei");
	private MenuItem addFolderItem = new MenuItem("Neuer Ordner");
	private MenuItem renameItem = new MenuItem("Umbenennen");
	private MenuItem deleteItem = new MenuItem("Löschen");
	private MenuItem renameItem2 = new MenuItem("Umbenennen");
	private MenuItem deleteItem2 = new MenuItem("Löschen");
	
	
	public TreeFileCell() {
		
		treeViewProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue != null && !newValue.editableProperty().get()) {
				addFileItem.setDisable(true);
				addFolderItem.setDisable(true);
				renameItem.setDisable(true);
				deleteItem.setDisable(true);
				renameItem2.setDisable(true);
				deleteItem2.setDisable(true);
			} else {
				addFileItem.setDisable(false);
				addFolderItem.setDisable(false);
				renameItem.setDisable(false);
				deleteItem.setDisable(false);
				renameItem2.setDisable(false);
				deleteItem2.setDisable(false);
			}
		});
		
		
		folderMenu.getItems().add(addFileItem);
		addFileItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				addFile();
			}
		});
		
		folderMenu.getItems().add(addFolderItem);
		addFolderItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				addFolder();
			}
		});
		
		folderMenu.getItems().add(renameItem);
		renameItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				rename();
			}
		});
		
		folderMenu.getItems().add(deleteItem);
		deleteItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				delete();
			}
		});
		
		
		fileMenu.getItems().add(renameItem2);
		renameItem2.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				rename();
			}
		});
		
		fileMenu.getItems().add(deleteItem2);
		deleteItem2.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				delete();
			}
		});
	}
	
	
	public void addFile() {
		startEdit();
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
	}
	
	
	public void addFolder() {
		startEdit();
		String result = Dialog.textInput("Bitte den Namen eingeben", "Neuer Ordner");
		
		File dir = new File(getItem().getPath() + "/" + result);
		if (dir.exists()) {
			Dialog.error("Dieser Ordner existiert schon.");
			return;
		}
		
		dir.mkdir();
		
		commitEdit(new File(""));
	}
	
	
	public void rename() {
		startEdit();
		String result = Dialog.textInput("Bitte den neuen Dateinamen eingeben", "Umbenennen", getItem().getName());
		
		File file = new File(getItem().getParent() + "/" + result);
		if (result.equals(getItem().getName())) {
			return;
		}
		if (file.exists()) {
			Dialog.error("Diese Datei existiert schon.");
			return;
		}
		
		if (result != null) {
			getItem().renameTo(file);
		}
		commitEdit(file);
	}
	
	
	public void delete() {
		startEdit();
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
	
	
	
	public String getString() {
		return getItem() == null ? "" : getItem().getName();
	}
	
}

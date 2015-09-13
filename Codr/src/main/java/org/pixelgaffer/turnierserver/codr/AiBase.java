/*
 * AiBase.java
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
package org.pixelgaffer.turnierserver.codr;


import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import org.apache.commons.lang.NotImplementedException;


/**
 * Grundklasse für KIs
 * 
 * @author Philip
 */
public class AiBase {
	
	public final String title;
	public final AiMode mode;
	public String gametype;
	public String language;
	public String description = "(keine Beschreibung)";
	public ObservableList<Version> versions = FXCollections.observableArrayList();
	
	
	public static enum AiMode {
		saved, online, simplePlayer, extern, fake
	}
	
	
	public static enum NewVersionType {
		fromFile, simplePlayer, lastVersion
	}
	
	
	/**
	 * Erstellt eine neue Ai
	 * 
	 * @param tit der übergebene Titel
	 */
	protected AiBase(String tit, AiMode mmode) {
		title = tit;
		mode = mmode;
		gametype = MainApp.actualGameType.get();
	}
	
	
	/**
	 * gibt die neueste Version oder null zurück
	 * 
	 * @return gibt null zurück, wenn es keine Version gibt
	 */
	public Version lastVersion() {
		if (versions.size() > 0) {
			return versions.get(versions.size() - 1);
		} else {
			return null;
		}
	}
	
	
	/**
	 * Gibt das gespeicherte Bild des Spielers zurück.
	 * 
	 * @return das gespeicherte Bild
	 */
	public ObjectProperty<Image> getPicture() {
		throw new NotImplementedException();
	}
	
	
	/**
	 * Speichert das Bild des Spielers in der Datei picture.png.
	 * 
	 * @param img das zu speichernde Bild
	 */
	public void setPicture(Image img) {
		throw new NotImplementedException();
	}
	
	
	/**
	 * Speichert das Bild des Spielers in der Datei picture.png.
	 * 
	 * @param img das zu speichernde Bild
	 */
	public void setPicture(File file) {
		throw new NotImplementedException();
	}
	
	
	/**
	 * damit die Ai-Liste richtig angezeigt wird
	 */
	public String toString() {
		return title;
	}
	
}

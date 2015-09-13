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

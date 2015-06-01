package org.pixelgaffer.turnierserver.esu;

import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Paths;
import org.pixelgaffer.turnierserver.esu.utilities.Resources;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;


public class Player {
	
	public final String title;
	public Language language;
	public String description = "(keine Beschreibung)";
	public ObservableList<Version> versions = FXCollections.observableArrayList();

	public static enum Language{
		Java, Python
	}
	
	public static enum NewVersionType{
		fromFile, simplePlayer, lastVersion
	}
	
	/**
	 * L�dt einen Player mit dem �bergebenen Titel in das Objekt.
	 * 
	 * @param tit der �bergebene Titel
	 */
	public Player(String tit){
		title = tit;
		loadProps();
		loadVersions();
	}
	/**
	 * Speichert einen neuen Player mit dem �bergebenen Titel und der Sprache ab.
	 * 
	 * @param tit der �bergebene Titel
	 * @param lang die �bergebene Sprache
	 */
	public Player(String tit, Language lang){
		title = tit;
		language = lang;
		
		File dir = new File(Paths.player(this));
		if (!dir.mkdirs()){
			ErrorLog.write("Der Spieler existiert bereits.");
			description = "invalid";
		}
		else{
			storeProps();
		}
	}
	
	/**
	 * F�gt eine neue Version der Versionsliste hinzu.
	 * 
	 * @param type die Art, in der die Version hinzugef�gt werden soll
	 * @return die Version, die hinzugef�gt wurde
	 */
	public Version newVersion(NewVersionType type){
		if (type == NewVersionType.fromFile){
			return null;
		}
		return newVersion(type, "");
	}
	
	/**
	 * gibt die neueste Version oder null zur�ck
	 * 
	 * @return gibt null zur�ck, wenn es keine Version gibt
	 */
	public Version lastVersion(){
		if (versions.size() > 0){
			return versions.get(versions.size()-1);
		}
		else{
			return null;
		}
	}
	
	/**
	 * F�gt eine neue Version der Versionsliste hinzu.
	 * 
	 * @param type die Art, in der die Version hinzugef�gt werden soll
	 * @param path der Pfad, von dem die Version kopiert werden soll, falls type==fromFile
	 * @return die Version, die hinzugef�gt wurde
	 */
	public Version newVersion(NewVersionType type, String path){
		Version version = null;
		switch (type){
		case fromFile:
			version = new Version(this, versions.size(), path);
			break;
		case lastVersion:
			if(versions.size() == 0){
				return null;
			}
			version = new Version(this, versions.size(), Paths.version(this, versions.size()-1));
			break;
		case simplePlayer:
			version = new Version(this, versions.size());
			break;
		}
		versions.add(version);
		storeProps();
		return version;
	}
	
	
	
	/**
	 * L�dt aus dem Dateiverzeichnis die Eigenschaften des Players.
	 */
	public void loadProps(){
		try {
			Reader reader = new FileReader(Paths.playerProperties(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			description = prop.getProperty("description");
			language = Language.valueOf(prop.getProperty("language"));
		} catch (IOException e) {ErrorLog.write("Fehler bei Laden aus der properties.txt");}
	}
	/**
	 * Speichert die Eigenschaften des Players in das Dateiverzeichnis.
	 */
	public void storeProps(){
		
		Properties prop = new Properties();
		prop.setProperty("description", description);
		prop.setProperty("versionAmount", "" + versions.size());
		prop.setProperty("language", language.toString());
		
		try {
			Writer writer = new FileWriter(Paths.playerProperties(this));
			prop.store(writer, title);
			writer.close();
		} catch (IOException e) {ErrorLog.write("Es kann keine Properties-Datei angelegt werden. (Player)");}
	}
	
	/**
	 * L�dt die Versionen aus dem Dateisystem, mit Hilfe der versionAmount-Property
	 */
	public void loadVersions(){
		versions.clear();
		int versionAmount = 0;
		try {
			Reader reader = new FileReader(Paths.playerProperties(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			versionAmount = Integer.parseInt(prop.getProperty("versionAmount"));
		} catch (IOException e) {ErrorLog.write("Fehler bei Laden aus der properties.txt (loadVerions)");}
		for (int i = 0; i < versionAmount; i++){
			versions.add(new Version(this, i));
		}
	}
	
	/**
	 * Setzt die Player-Beschreibung.
	 * 
	 * @param des die Beschreibung des Players
	 */
	public void setDescription(String des){
		description = des;
		storeProps();
	}
	
	/**
	 * Gibt das gespeicherte Bild des Spielers zur�ck.
	 * 
	 * @return das gespeicherte Bild
	 */
	public Image getPicture(){
		Image img = Resources.imageFromFile(Paths.playerPicture(this));
		if (img == null){
			img = Resources.defaultPicture();
		}
		return img;
	}
	/**
	 * Speichert das Bild des Spielers in der Datei picture.png.
	 * 
	 * @param img das zu speichernde Bild
	 */
	public void setPicture(Image img){
		try {
			if (img == null){
				File file = new File(Paths.playerPicture(this));
				file.delete();
			}
			else{
				ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(Paths.playerPicture(this)));
			}
		} catch (IOException e) {
			ErrorLog.write("Bild konnte nicht gespeichert werden.");
		}
	}
	
	/**
	 * damit die Player-Liste richtig angezeigt wird
	 */
	public String toString(){
		return title;
	}
		
}
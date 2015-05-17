package org.pixelgaffer.turnierserver.esu;

import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import org.pixelgaffer.turnierserver.esu.MainApp.Language;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;


public class Player {
	
	public final String title;
	public Language language;
	private String description = "(keine Beschreibung)";
	List<Version> versions = new ArrayList<Version>();
	
	public static enum NewVersionType{
		fromFile, simplePlayer, lastVersion
	}
	
	/**
	 * Lädt einen Player mit dem übergebenen Titel in das Objekt.
	 * 
	 * @param tit der übergebene Titel
	 */
	public Player(String tit){
		title = tit;
		loadProps();
	}
	/**
	 * Speichert einen neuen Player mit dem übergebenen Titel und der Sprache ab.
	 * 
	 * @param tit der übergebene Titel
	 * @param lang die übergebene Sprache
	 */
	public Player(String tit, Language lang){
		title = tit;
		language = lang;
		storeProps();
	}
	
	/**
	 * Fügt eine neue Version der Versionsliste hinzu.
	 * 
	 * @param type die Art, in der die Version hinzugefügt werden soll
	 * @return die Version, die hinzugefügt wurde
	 */
	public Version newVersion(NewVersionType type){
		if (type == NewVersionType.fromFile){
			return null;
		}
		return newVersion(type, "");
	}
	/**
	 * Fügt eine neue Version der Versionsliste hinzu.
	 * 
	 * @param type die Art, in der die Version hinzugefügt werden soll
	 * @param path der Pfad, von dem die Version kopiert werden soll, falls type==fromFile
	 * @return die Version, die hinzugefügt wurde
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
			version = new Version(this, versions.size(), "Players\\" + title + "\\v" + (versions.size()-1));
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
	 * Lädt aus dem Dateiverzeichnis die Eigenschaften des Players.
	 */
	public void loadProps(){
		try {
			Reader reader = new FileReader("Players\\" + title + "\\properties.txt");
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			description = prop.getProperty("description");
			switch(prop.getProperty("language")){
			case "Java":
				language = Language.Java;
				break;
			case "Phython":
				language = Language.Phyton;
				break;
			default:
				language = Language.Java;
			}
		} catch (IOException e) {ErrorLog.write("Fehler bei Laden aus der properties.txt");}
	}
	/**
	 * Speichert die Eigenschaften des Players in das Dateiverzeichnis.
	 */
	public void storeProps(){
		File dir = new File("Players\\" + title);
		dir.mkdirs();
		
		Properties prop = new Properties();
		prop.setProperty("description", description);
		prop.setProperty("versionAmount", "" + versions.size());
		switch (language){
		case Java:
			prop.setProperty("language", "Java");
			break;
		case Phyton:
			prop.setProperty("language", "Phyton");
			break;
		}
		
		try {
			Writer writer = new FileWriter("Players\\" + title + "\\properties.txt");
			prop.store(writer, title );
			writer.close();
		} catch (IOException e) {ErrorLog.write("Es kann keine Properties-Datei angelegt werden. (Player)");}
	}
	
	public void loadVersions(){
		versions.clear();
		int versionAmount = 0;
		try {
			Reader reader = new FileReader("Players\\" + title + "\\properties.txt");
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
	 * Gibt die Player-Beschreibung zurück.
	 * 
	 * @return die Player-Beschreibung
	 */
	public String getDescription(){
		loadProps();
		return description;
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
	 * Gibt das gespeicherte Bild des Spielers zurück.
	 * 
	 * @return das gespeicherte Bild
	 */
	public Image getPicture(){
		try {
			FileInputStream fin = new FileInputStream(title + "\\picture.png");
			Image img = new Image(fin);
			fin.close();
			return img;
		} catch (Exception e) {
			try {
				return new Image(getClass().getResourceAsStream("default_ai.png"));
			} catch (Exception ex) {
				ErrorLog.write("Default-Bild konnte nicht geladen werden.");
				return null;
			}
		}
	}
	/**
	 * Speichert das Bild des Spielers in der Datei picture.png.
	 * 
	 * @param img das zu speichernde Bild
	 */
	public void setPicture(Image img){
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(title + "\\picture.png"));
		} catch (IOException e) {
			ErrorLog.write("Bild konnte nicht gespeichert werden.");
		}
	}
		
}
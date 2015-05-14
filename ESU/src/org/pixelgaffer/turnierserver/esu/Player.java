package org.pixelgaffer.turnierserver.esu;

import java.io.*;
import java.util.*;

import org.pixelgaffer.turnierserver.esu.MainApp.Language;

import javafx.scene.image.Image;

public class Player {
	
	private final String title;
	public final Language language;
	private Properties prop;
	List<Version> versions = new ArrayList<Version>();
	

	public Player(String tit){
		title = tit;
		language = Language.Java;
		try {
			Reader reader = new FileReader(title + "\\properties.txt");
			prop = new Properties();
			prop.load(reader);
			ErrorLog.write(prop.getProperty("language"), true);
		} catch (IOException e) {ErrorLog.write("Dieser Spieler existiert nicht.");}
	}
	public Player(String tit, Language lang){
		title = tit;
		language = lang;
		File dir = new File(title);
		if(dir.mkdir()){
			prop = new Properties();
			prop.setProperty("title", title);
			switch (language){
			case Java:
				prop.setProperty("language", "Java");
				break;
			case Phyton:
				prop.setProperty("language", "Phyton");
				break;
			}
			
			try {
				Writer writer = new FileWriter(title + "\\properties.txt");
				prop.store(writer, "Datei" );
			} catch (IOException e) {ErrorLog.write("Es kann keine Properties-Datei angelegt werden.");}
			ErrorLog.write("Ein neuer Ordner wurde angelegt.");
		}
		else{
			ErrorLog.write("Dieser Ordner existiert bereits.");
		}
		
	}
	
	public String getDescription(){
		return null;
	}
	public void setDescription(){
		
	}
	public Image getPicture(){
		return null;
	}
	public void setPicture(Image img){
		
	}
	
	
}

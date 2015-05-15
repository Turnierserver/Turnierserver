package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ErrorLog {

	private static final File file = new File("errorLog.txt");
	
	/**
	 * löscht die Datei "errorLog.txt"
	 */
	public static void clear(){
		file.delete();
	}
	
	/**
	 * Speichert einen neuen Eintrag in "errorLog.txt"
	 * 
	 * @param log Inhalt der Fehlermeldung
	 * @param withClock bestimmt, ob die Uhrzeit mit gespeichert werden soll
	 */
	public static void write(String log, boolean withClock){
		if (withClock){
			Date now = new Date();
			writeWithoutBlanck(new java.text.SimpleDateFormat("HH:mm,ss").format(now));
			writeWithoutBlanck(": " + log);
			writeWithoutBlanck(System.getProperty("line.separator"));
		}
		else{
			write(log);
		}
	}
	
	/**
	 * Überladene Methode ohne withClock-Auswahl
	 * 
	 * @param log Inhalt der Fehlermeldung
	 */
	public static void write(String log){
		writeWithoutBlanck(log);
		writeWithoutBlanck(System.getProperty("line.separator"));
	}
	
	/**
	 * Übernimmt den eigentlichen Vorgang des Schreibens
	 * 
	 * @param log Inhalt der Fehlermeldung
	 */
	private static void writeWithoutBlanck(String log){
		try {
			FileWriter writer = new FileWriter(file ,true);
			writer.write(log);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

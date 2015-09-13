/*
 * ErrorLog.java
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
package org.pixelgaffer.turnierserver.codr.utilities;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


/**
 * Stellt eine Methode bereit, die einen ErrorLog in eine Datei speichert. (und in der Konsole ausgibt)
 * 
 * @author Philip
 */
public class ErrorLog {
	
	private static final File file = new File("errorLog.txt");
	
	
	/**
	 * löscht die Datei "errorLog.txt"
	 */
	public static void clear() {
		file.delete();
	}
	
	
	/**
	 * Speichert einen neuen Eintrag in "errorLog.txt" mit anschließender newLine
	 * 
	 * @param log Inhalt der Fehlermeldung
	 * @param withClock bestimmt, ob die Uhrzeit mit gespeichert werden soll
	 */
	public static void write(String log, boolean withClock) {
		if (withClock) {
			writeWithoutBlanck(new java.text.SimpleDateFormat("HH:mm,ss").format(new Date()) + ": ");
			write(log);
		} else {
			write(log);
		}
	}
	
	
	/**
	 * überladene Methode ohne withClock-Auswahl (siehe oben)
	 * 
	 * @param log Inhalt der Fehlermeldung
	 */
	public static void write(String log) {
		writeWithoutBlanck(log);
		writeWithoutBlanck(System.getProperty("line.separator"));
	}
	
	
	/**
	 * übernimmt den eigentlichen Vorgang des Schreibens
	 * 
	 * @param log Inhalt der Fehlermeldung
	 */
	private static void writeWithoutBlanck(String log) {
		try {
			System.out.print(log);
			FileWriter writer = new FileWriter(file, true);
			writer.write(log);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

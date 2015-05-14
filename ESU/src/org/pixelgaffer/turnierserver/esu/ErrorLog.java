package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ErrorLog {

	private static FileWriter writer;
	private static File file = new File("errorLog.txt");
		
	public static void clear(){
		file.delete();
	}

	public static void write(String log, boolean withClock){
		if (withClock){
			Date now = new Date();
			writeWithoutBlanck(new java.text.SimpleDateFormat("HH:mm ss").format(now));
			writeWithoutBlanck(": " + log);
			writeWithoutBlanck(System.getProperty("line.separator"));
		}
		else{
			write(log);
		}
	}
	
	public static void write(String log){
		writeWithoutBlanck(log);
		writeWithoutBlanck(System.getProperty("line.separator"));
	}
	
	private static void writeWithoutBlanck(String log){
		try {
			writer = new FileWriter(file ,true);
			writer.write(log);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

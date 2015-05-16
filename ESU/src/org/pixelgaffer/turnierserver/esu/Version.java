package org.pixelgaffer.turnierserver.esu;

import java.io.*;
import java.nio.channels.FileChannel;

public class Version {
	
	public final Player player;
	public final int number;
	
	public Version(Player p, int n){
		player = p;
		number = n;
		String path = "Downloads\\SimplePlayer\\";
		switch (player.language){
		case Java:
			path += "Java";
			break;
		case Phyton:
			path += "Python";
			break;
		}
		copyFromFile(path);
	}
	public Version(Player p, int n, String path){
		player = p;
		number = n;
		copyFromFile(path);
	}
	
	
	public void copyFromFile(String path){
		File newDir = new File("Players\\" + player.title + "\\v" + number);
		newDir.mkdirs();
		
		File f = new File(path);
		File[] fileArray = f.listFiles();
		if (fileArray == null){
			ErrorLog.write("Version konnte nicht kopiert werden");
			return;
		}
		
		for (int i = 0; i < fileArray.length; i++){
			File out = new File("Players\\" + player.title + "\\v" + number + "\\" + fileArray[i].getName());
			copyFile(fileArray[i], out);
		}
	}
	
	public static void copyFile(File in, File out){
		FileChannel inChannel = null;
		FileChannel outChannel = null;
        try {
        	FileInputStream fin = new FileInputStream(in);
			FileOutputStream fout = new FileOutputStream(out);
			inChannel = fin.getChannel();
	        outChannel = fout.getChannel();
	        fin.close();
	        fout.close();
            inChannel.transferTo(0, inChannel.size(),outChannel);////////////todo: Inhalt wird nicht mitkopiert!!!!!!!!
        } 
        catch (IOException e) {}
        finally {
        	
            try {
				if (inChannel != null) inChannel.close();
				if (outChannel != null) outChannel.close();
			} catch (IOException e) {
				ErrorLog.write("Dateien konnten nicht kopiert werden.");
			}
        }
    }
	
	
	
}

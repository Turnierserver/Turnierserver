package org.pixelgaffer.turnierserver.esu;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

import org.pixelgaffer.turnierserver.esu.MainApp.Language;

public class Version {
	
	public final Player player;
	public final int number;
	public boolean compiled = false;
	public boolean qualified = false;
	public boolean finished = false;
	public boolean uploaded = false;
	
	public Version(Player p, int n){
		player = p;
		number = n;
		
		if (!exists()){
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
			storeProps();
		}
	}
	public Version(Player p, int n, String path){
		player = p;
		number = n;
		
		copyFromFile(path);
		storeProps();
		
	}
	
	public boolean exists(){
		File dir = new File("Players\\" + player.title + "\\v" + number);
		return !dir.mkdirs();
	}
	
	public void copyFromFile(String path){
		Path srcPath = new File(path).toPath();
		Path destPath = new File("Players\\" + player.title + "\\v" + number).toPath();
		try {
			Files.walkFileTree(srcPath, new CopyVisitor(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING));
		} catch (IOException e) {
			ErrorLog.write("Version konnte nicht kopiert werden");
		}
	}
	
	

	/**
	 * Lädt aus dem Dateiverzeichnis die Eigenschaften des Players.
	 */
	public void loadProps(){
		try {
			Reader reader = new FileReader("Players\\" + player.title + "\\v" + number + "\\properties.txt");
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			compiled = Boolean.parseBoolean(prop.getProperty("compiled"));
			qualified = Boolean.parseBoolean(prop.getProperty("qualified"));
			finished = Boolean.parseBoolean(prop.getProperty("finished"));
			uploaded = Boolean.parseBoolean(prop.getProperty("uploaded"));
		} catch (IOException e) {ErrorLog.write("Fehler bei Laden aus der properties.txt (Version)");}
	}
	/**
	 * Speichert die Eigenschaften des Players in das Dateiverzeichnis.
	 */
	public void storeProps(){
		Properties prop = new Properties();
		prop.setProperty("compiled", "" + compiled);
		prop.setProperty("qualified", "" + qualified);
		prop.setProperty("finished", "" + finished);
		prop.setProperty("uploaded", "" + uploaded);
		
		try {
			Writer writer = new FileWriter("Players\\" + player.title + "\\v" + number + "\\properties.txt");
			prop.store(writer, player.title + " v" + number );
			writer.close();
		} catch (IOException e) {ErrorLog.write("Es kann keine Properties-Datei angelegt werden. (Version)");}
	}
	
	
	
	
	
	
	public static class CopyVisitor extends SimpleFileVisitor<Path>
	{
	    private final Path fromPath;
	    private final Path toPath;
	    private final CopyOption copyOption;

	    public CopyVisitor(Path fromPath, Path toPath, CopyOption copyOption)
	    {
	        this.fromPath = fromPath;
	        this.toPath = toPath;
	        this.copyOption = copyOption;
	    }

	    @Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
	    {
	        Path targetPath = toPath.resolve(fromPath.relativize(dir));
	        if( !Files.exists(targetPath) )
	        {
	            Files.createDirectory(targetPath);
	        }
	        return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
	    {
	        Files.copy(file, toPath.resolve(fromPath.relativize(file)), copyOption);
	        return FileVisitResult.CONTINUE;
	    }
	}
	
	
}

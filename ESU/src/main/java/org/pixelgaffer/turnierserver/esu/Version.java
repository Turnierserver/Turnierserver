package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONObject;
import org.pixelgaffer.turnierserver.esu.Ai.AiMode;
import org.pixelgaffer.turnierserver.esu.Game.GameMode;
import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Paths;


public class Version {
	
	public final Ai ai;
	public final int number;
	public final AiMode mode;
	
	public boolean compiled = false;
	public boolean qualified = false;
	public boolean finished = false;
	public boolean uploaded = false;
	public String compileOutput = "";
	public String qualifyOutput = "";
	public List<CodeEditor> files = new ArrayList<CodeEditor>();
	
	
	public Version(Ai p, int n, JSONObject json){
		ai = p;
		number = n;
		mode = AiMode.online;
		compiled = json.getBoolean("compiled");
		qualified = json.getBoolean("qualified");
		finished = json.getBoolean("frozen");
	}
	
	/**
	 * Erstellt eine neue Version und lädt automatisch den Quellcode
	 * 
	 * @param p der Spieler
	 * @param n die Nummer
	 */
	public Version(Ai p, int n, AiMode mmode){
		ai = p;
		number = n;
		mode = mmode;
		
		if (mode == AiMode.saved || mode == AiMode.simplePlayer){
			if (!exists()){
				ai.gametype = MainApp.actualGameType.get();
				copyFromFile(Paths.simplePlayer("" + ai.gametype, ai.language));
				storeProps();
				findCode();
			}
			else{
				loadProps();
				findCode();
			}
		}
	}
	public Version(Ai p, int n, String path){
		ai = p;
		number = n;
		mode = AiMode.saved;
		
		exists();
		
		copyFromFile(path);
		storeProps();
		findCode();
	}
	
	/**
	 * Prüft, ob die Version bereits im Dateisystem existiert.
	 * 
	 * @return true, wenn die Version bereits existiert
	 */
	public boolean exists(){
		if (mode != AiMode.saved){
			if (mode != AiMode.simplePlayer)
				ErrorLog.write("dies ist kein speicherbares Objekt (exists)");
			return true;
		}
		File dir = new File(Paths.version(this));
		return !dir.mkdirs();
	}
	
	/**
	 * Kopiert alle Dateien von einem bestimmten Pfad in das Verzeichnis der Version.
	 * 
	 * @param path der Pfad, von dem kopiert werden soll
	 */
	public void copyFromFile(String path){
		if (mode != AiMode.saved){
			ErrorLog.write("dies ist kein speicherbares Objekt (copyFromFile)");
			return;
		}
		Path srcPath = new File(path).toPath();
		Path destPath = new File(Paths.version(this)).toPath();
		try {
			Files.walkFileTree(srcPath, new CopyVisitor(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING));
		} catch (IOException e) {
			e.printStackTrace();
			ErrorLog.write("Version konnte nicht kopiert werden: "+ e.getMessage());
		}
	}
	
	/**
	 * Sucht alle Dateien innerhalb des Versionsordners und speichert sie in files
	 */
	public void findCode(){
		if (mode != AiMode.saved && mode != AiMode.simplePlayer) {
			ErrorLog.write("dies ist kein lesbares Objekt (findCode)");
			return;
		}
		Path path = new File(Paths.version(this)).toPath();
		VersionVisitor visitor = new VersionVisitor(path);
		try {
			Files.walkFileTree(path, visitor);
			files = visitor.files;
		} catch (IOException e) {
			ErrorLog.write("Dateien der Version konnten nicht geladen werden.");
		}
	}
	
	/**
	 * Speichert alle Dateien aus den CodeEditoren in files im Dateisystem ab
	 */
	public void saveCode(){
		if (mode != AiMode.saved){
			if (mode != AiMode.simplePlayer)
				ErrorLog.write("dies ist kein speicherbares Objekt (saveCode)");
			return;
		}
		for (int i = 0; i < files.size(); i++){
			files.get(i).save();
		}
	}
	
	

	/**
	 * Lädt aus dem Dateiverzeichnis die Eigenschaften des Players.
	 */
	public void loadProps(){
		if (mode != AiMode.saved && mode != AiMode.simplePlayer) {
			ErrorLog.write("dies ist kein lesbares Objekt (Version.loadProps)");
			return;
		}
		try {
			Reader reader = new FileReader(Paths.versionProperties(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			compiled = Boolean.parseBoolean(prop.getProperty("compiled"));
			qualified = Boolean.parseBoolean(prop.getProperty("qualified"));
			finished = Boolean.parseBoolean(prop.getProperty("finished"));
			uploaded = Boolean.parseBoolean(prop.getProperty("uploaded"));
			compileOutput = prop.getProperty("compileOutput");
			qualifyOutput = prop.getProperty("qualifyOutput");
		} catch (IOException e) {
			ErrorLog.write("Fehler bei Laden aus der properties.txt (Version)");}
	}
	/**
	 * Speichert die Eigenschaften des Players in das Dateiverzeichnis.
	 */
	public void storeProps(){
		if (mode != AiMode.saved){
			ErrorLog.write("dies ist kein speicherbares Objekt (Version.storeProps)");
			return;
		}
		Properties prop = new Properties();
		prop.setProperty("compiled", "" + compiled);
		prop.setProperty("qualified", "" + qualified);
		prop.setProperty("finished", "" + finished);
		prop.setProperty("uploaded", "" + uploaded);
		prop.setProperty("compileOutput", compileOutput);
		prop.setProperty("qualifyOutput", qualifyOutput);
		
		try {
			Writer writer = new FileWriter(Paths.versionProperties(this));
			prop.store(writer, ai.title + " v" + number );
			writer.close();
		} catch (IOException e) {ErrorLog.write("Es kann keine Properties-Datei angelegt werden. (Version)");}
	}
	
	
	/**
	 * Kompiliert die Quellcodedateien
	 * 
	 * @return false, wenn die Kompilierung fehlgeschlagen ist
	 */
	public boolean compile(){
		if (mode != AiMode.saved){
			ErrorLog.write("dies ist kein speicherbares Objekt (compile)");
			return false;
		}
		saveCode();
		compiled = true;
		compileOutput = "Kompilierung fertig!";
		storeProps();
		return true;
	}
	
	/**
	 * Qualifiziert die Ki
	 * 
	 * @return false, wenn die Qualifikation fehlgeschlagen ist
	 */
	public boolean qualify(){
		if (mode != AiMode.saved){
			ErrorLog.write("dies ist kein speicherbares Objekt (qualify)");
			return false;
		}
		if (!compiled)
			if (!compile())
				return false;
		
		qualified = true;
		qualifyOutput = "Qualifikation fertig!";
		storeProps();
		return true;
	}
	
	/**
	 * Stellt die Ki fertig, was bedeutet, dass sie nicht mehr bearbeitet werden kann.
	 */
	public void finish(){
		if (mode != AiMode.saved){
			ErrorLog.write("dies ist kein speicherbares Objekt (finish)");
			return;
		}
		finished = true;
		storeProps();
	}
	
	
	
	
	/**
	 * damit in der ChoiceBox die Nummer angezeigt wird
	 */
	public String toString(){
		return "" + number;
	}
	
	/**
	 * Ein FileVisitor, der eine Datei bei ihrem Besuch kopiert
	 * 
	 * http://codingjunkie.net/java-7-copy-move/
	 */
	public static class CopyVisitor extends SimpleFileVisitor<Path>
	{
	    private final Path fromPath;
	    private final Path toPath;
	    private final CopyOption copyOption;

	    public CopyVisitor(Path _fromPath, Path _toPath, CopyOption _copyOption)
	    {
	        fromPath = _fromPath;
	        toPath = _toPath;
	        copyOption = _copyOption;
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
	

	/**
	 * Ein FileVisitor, der jede Datei als Version abspeichert
	 */
	public static class VersionVisitor extends SimpleFileVisitor<Path>
	{
	    private final Path path;
	    public List<CodeEditor> files = new ArrayList<CodeEditor>();

	    public VersionVisitor(Path _path)
	    {
	    	path = _path;
	    }

	    @Override
	    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
	    {
	        return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	    {
	        files.add(new CodeEditor(file.toFile()));
	        return FileVisitResult.CONTINUE;
	    }
	}
	
	
}

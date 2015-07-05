package org.pixelgaffer.turnierserver.codr;

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

import javafx.scene.control.TreeItem;

import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.AiBase.AiMode;
import org.pixelgaffer.turnierserver.codr.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.codr.utilities.Libraries;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;
import org.pixelgaffer.turnierserver.compile.CompileFailureException;
import org.pixelgaffer.turnierserver.compile.Compiler;

public class Version
{
	
	public final AiBase ai;
	public final int number;
	
	public String executeCommand[] = new String[0];
	public boolean compiled = false;
	public boolean qualified = false;
	public boolean finished = false;
	public boolean uploaded = false;
	public String compileOutput = "";
	public String qualifyOutput = "";
	public List<CodeEditor> files = new ArrayList<CodeEditor>();
	public TreeItem<File> rootFile = null;
	
	public Version (AiBase aai, int n, JSONObject json)
	{
		ai = aai;
		number = n;
		
		compiled = json.getBoolean("compiled");
		qualified = json.getBoolean("qualified");
		finished = json.getBoolean("frozen");
	}
	
	/**
	 * Instanziert eine neue Version und lädt automatisch den Quellcode
	 * 
	 * @param p der Spieler
	 * @param n die Nummer
	 */
	public Version (AiBase aai, int n, AiMode mmode)
	{
		ai = aai;
		number = n;
		
		if (ai.mode == AiMode.saved || ai.mode == AiMode.simplePlayer)
		{
			if (!exists())
			{
				ai.gametype = MainApp.actualGameType.get();
				copyFromFile(Paths.simplePlayer("" + ai.gametype, ai.language));
				storeProps();
				findCode();
			}
			else
			{
				loadProps();
				findCode();
			}
		}
	}
	
	public Version (AiBase aai, int n, AiMode mmode, String path)
	{
		ai = aai;
		number = n;
		
		exists();
		
		if (ai.mode == AiMode.saved)
			copyFromFile(path);
		storeProps();
		findCode();
	}
	
	
	/**
	 * Prüft, ob die Version bereits im Dateisystem existiert.
	 * 
	 * @return true, wenn die Version bereits existiert
	 */
	public boolean exists ()
	{
		if (ai.mode != AiMode.saved && ai.mode != AiMode.extern && ai.mode != AiMode.simplePlayer)
		{
			ErrorLog.write("dies ist kein speicherbares Objekt (exists)");
			return true;
		}
		File dir = new File(Paths.version(this));
		return !dir.mkdirs();
	}
	
	
	/**
	 * Kopiert alle Dateien von einem bestimmten Pfad in das Verzeichnis der
	 * Version.
	 * 
	 * @param path der Pfad, von dem kopiert werden soll
	 */
	public void copyFromFile (String path)
	{
		if (ai.mode != AiMode.saved && ai.mode != AiMode.extern)
		{
			ErrorLog.write("dies ist kein speicherbares Objekt (copyFromFile)");
			return;
		}
		Path srcPath = new File(path).toPath();
		Path destPath = new File(Paths.version(this)).toPath();
		try
		{
			Files.walkFileTree(srcPath, new CopyVisitor(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			ErrorLog.write("Version konnte nicht kopiert werden: " + e.getMessage());
		}
	}
	
	
	/**
	 * Sucht alle Dateien innerhalb des Versionsordners und speichert sie in
	 * files
	 */
	public void findCode ()
	{
		if (ai.mode != AiMode.saved && ai.mode != AiMode.simplePlayer && ai.mode != AiMode.extern)
		{
			ErrorLog.write("dies ist kein lesbares Objekt (findCode)");
			return;
		}
		
		files.clear();
		rootFile = new TreeItem<File>(new File(Paths.versionSrc(this)));
		rootFile.setExpanded(true);
		recursiveFileBuild(rootFile);
	}
	
	
	private void recursiveFileBuild (TreeItem<File> item)
	{
		File[] underFiles = item.getValue().listFiles();
		
		if (underFiles == null)
			return;
		
		for (File file : underFiles)
		{
			if (file.getName().startsWith("."))
			{
				continue;
			}
			
			TreeItem<File> actual = new TreeItem<File>(file);
			actual.setExpanded(true);
			item.getChildren().add(actual);
			if (file.isDirectory())
			{
				recursiveFileBuild(actual);
			}
			else
			{
				files.add(new CodeEditor(file));
			}
		}
	}
	
	
	/**
	 * Speichert alle Dateien aus den CodeEditoren in files im Dateisystem ab
	 */
	public void saveCode ()
	{
		if (ai.mode != AiMode.saved && ai.mode != AiMode.extern)
		{
			if (ai.mode != AiMode.simplePlayer)
				ErrorLog.write("dies ist kein speicherbares Objekt (saveCode)");
			return;
		}
		if (finished == true)
		{
			ErrorLog.write("Man kann den Code einer fertiggestellten Version nicht speichern");
			return;
		}
		
		for (int i = 0; i < files.size(); i++)
		{
			files.get(i).save();
		}
	}
	
	
	/**
	 * Lädt aus dem Dateiverzeichnis die Eigenschaften des Players.
	 */
	public void loadProps ()
	{
		if (ai.mode != AiMode.saved && ai.mode != AiMode.simplePlayer && ai.mode != AiMode.extern)
		{
			ErrorLog.write("dies ist kein lesbares Objekt (Version.loadProps)");
			return;
		}
		try
		{
			Reader reader = new FileReader(Paths.versionProperties(this));
			Properties prop = new Properties();
			prop.load(reader);
			reader.close();
			executeCommand = new String[Integer.parseInt(prop.getProperty("executeCommand.size", "0"))];
			for (int i = 0; i < executeCommand.length; i++)
				executeCommand[i] = prop.getProperty("executeCommand." + i);
			compiled = Boolean.parseBoolean(prop.getProperty("compiled"));
			qualified = Boolean.parseBoolean(prop.getProperty("qualified"));
			finished = Boolean.parseBoolean(prop.getProperty("finished"));
			uploaded = Boolean.parseBoolean(prop.getProperty("uploaded"));
			compileOutput = prop.getProperty("compileOutput");
			qualifyOutput = prop.getProperty("qualifyOutput");
		}
		catch (IOException e)
		{
			ErrorLog.write("Fehler bei Laden aus der properties.txt (Version)");
		}
	}
	
	
	/**
	 * Speichert die Eigenschaften des Players in das Dateiverzeichnis.
	 */
	public void storeProps ()
	{
		if (ai.mode != AiMode.saved && ai.mode != AiMode.extern)
		{
			ErrorLog.write("dies ist kein speicherbares Objekt (Version.storeProps)");
			return;
		}
		Properties prop = new Properties();
		prop.setProperty("executeCommand.size", Integer.toString(executeCommand.length));
		for (int i = 0; i < executeCommand.length; i++)
			prop.setProperty("executeCommand." + i, executeCommand[i]);
		prop.setProperty("compiled", "" + compiled);
		prop.setProperty("qualified", "" + qualified);
		prop.setProperty("finished", "" + finished);
		prop.setProperty("uploaded", "" + uploaded);
		prop.setProperty("compileOutput", compileOutput);
		prop.setProperty("qualifyOutput", qualifyOutput);
		
		try
		{
			Writer writer = new FileWriter(Paths.versionProperties(this));
			prop.store(writer, ai.title + " v" + number);
			writer.close();
		}
		catch (IOException e)
		{
			ErrorLog.write("Es kann keine Properties-Datei angelegt werden. (Version)");
		}
	}
	
	
	/**
	 * Kompiliert die Quellcodedateien
	 * 
	 * @return false, wenn die Kompilierung fehlgeschlagen ist
	 */
	public boolean compile ()
	{
		if (ai.mode != AiMode.saved && ai.mode != AiMode.simplePlayer && ai.mode != AiMode.extern)
		{
			ErrorLog.write("dies ist kein kompilierbares Objekt (compile)");
			return false;
		}
		saveCode();
		
		try
		{
			Compiler c = Compiler.getCompiler(ai.language);
			compileOutput = c.compile(new File(Paths.versionSrc(this)), new File(Paths.versionBin(this)), new File(
					Paths.versionSrcStartClass(this)), new Libraries());
			executeCommand = c.getCommand();
			compileOutput += "\nKompilierung erfolgreich\n";
			compiled = true;
		}
		catch (ReflectiveOperationException roe)
		{
			ErrorLog.write("Fehler beim Laden des Compilers: " + roe);
			compiled = false;
		}
		catch (InterruptedException | IOException e)
		{
			ErrorLog.write("Fehler beim Kompilieren: " + e);
			e.printStackTrace();
			compiled = false;
		}
		catch (CompileFailureException cfe)
		{
			compileOutput = cfe.getMessage();
			compileOutput += "\nKompilierung fehlgeschlagen\n";
			compiled = false;
		}
		
		storeProps();
		return compiled;
	}
	
	
	/**
	 * Qualifiziert die Ki
	 * 
	 * @return false, wenn die Qualifikation fehlgeschlagen ist
	 */
	public boolean qualify ()
	{
		if (ai.mode != AiMode.saved && ai.mode != AiMode.extern)
		{
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
	 * Stellt die Ki fertig, was bedeutet, dass sie nicht mehr bearbeitet werden
	 * kann.
	 */
	public void finish ()
	{
		if (ai.mode != AiMode.saved && ai.mode != AiMode.extern)
		{
			ErrorLog.write("dies ist kein speicherbares Objekt (finish)");
			return;
		}
		finished = true;
		storeProps();
	}
	
	
	/**
	 * damit in der ChoiceBox die Nummer angezeigt wird
	 */
	public String toString ()
	{
		return "" + number;
	}
	
	
	/**
	 * Ein FileVisitor, der eine Datei bei ihrem Besuch kopiert
	 * http://codingjunkie.net/java-7-copy-move/
	 */
	public static class CopyVisitor extends SimpleFileVisitor<Path>
	{
		
		private final Path fromPath;
		private final Path toPath;
		private final CopyOption copyOption;
		
		
		public CopyVisitor (Path _fromPath, Path _toPath, CopyOption _copyOption)
		{
			fromPath = _fromPath;
			toPath = _toPath;
			copyOption = _copyOption;
		}
		
		
		@Override
		public FileVisitResult preVisitDirectory (Path dir, BasicFileAttributes attrs) throws IOException
		{
			Path targetPath = toPath.resolve(fromPath.relativize(dir));
			if (!Files.exists(targetPath))
			{
				Files.createDirectory(targetPath);
			}
			return FileVisitResult.CONTINUE;
		}
		
		
		@Override
		public FileVisitResult visitFile (Path file, BasicFileAttributes attrs) throws IOException
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
		
		public TreeItem<File> rootFile;
		public List<CodeEditor> files = new ArrayList<CodeEditor>();
		
		
		public VersionVisitor (File file)
		{
			rootFile = new TreeItem<>(file);
		}
		
		
		@Override
		public FileVisitResult preVisitDirectory (Path dir, BasicFileAttributes attrs)
		{
			return FileVisitResult.CONTINUE;
		}
		
		
		@Override
		public FileVisitResult visitFile (Path file, BasicFileAttributes attrs)
		{
			if (file.toFile().getName().startsWith("."))
			{
				return FileVisitResult.CONTINUE;
			}
			if (file.toFile().getName().equals("libraries.txt"))
			{
				return FileVisitResult.CONTINUE;
			}
			files.add(new CodeEditor(file.toFile()));
			return FileVisitResult.CONTINUE;
		}
	}
	
}

package org.pixelgaffer.katepartparser;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

public class Test extends Application
{
	public static String exampleText = "package org.pixelgaffer.turnierserver.compile;\n" + "\n"
			+ "import it.sauronsoftware.ftp4j.FTPAbortedException;\n"
			+ "import it.sauronsoftware.ftp4j.FTPDataTransferException;\n"
			+ "import it.sauronsoftware.ftp4j.FTPException;\n"
			+ "import it.sauronsoftware.ftp4j.FTPIllegalReplyException;\n"
			+ "import it.sauronsoftware.ftp4j.FTPListParseException;\n" + "\n"
			+ "import java.io.File;\n" + "import java.io.FileInputStream;\n"
			+ "import java.io.FileOutputStream;\n" + "import java.io.FileReader;\n"
			+ "import java.io.FileWriter;\n" + "import java.io.IOException;\n"
			+ "import java.io.PrintWriter;\n" + "import java.io.Reader;\n"
			+ "import java.io.StringWriter;\n" + "import java.io.Writer;\n"
			+ "import java.nio.file.Files;\n" + "import java.nio.file.Path;\n"
			+ "import java.util.Properties;\n" + "import java.util.UUID;\n" + "\n"
			+ "import lombok.AccessLevel;\n" + "import lombok.Getter;\n"
			+ "import lombok.NonNull;\n" + "import lombok.RequiredArgsConstructor;\n"
			+ "import lombok.Setter;\n" + "\n"
			+ "import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;\n"
			+ "import org.pixelgaffer.turnierserver.networking.bwprotocol.WorkerCommandAnswer;\n"
			+ "import org.pixelgaffer.turnierserver.networking.messages.WorkerCommand;\n" + "\n"
			+ "/**\n"
			+ " * Diese Klasse ist eine abstrakte Implementation eines Compilers, der die\n"
			+ " * Verbindung zum FTP-Server größtenteils übernimmt.\n" + " */\n"
			+ "public abstract class Compiler\n" + "{\n"
			+ "\tpublic Compiler (int ai, int version, int game)\n" + "\t{\n" + "\t\tsuper();\n"
			+ "\t\tthis.ai = ai;\n" + "\t\tthis.version = version;\n" + "\t\tthis.game = game;\n"
			+ "\t}\n" + "\t\n"
			+ "\tpublic static Compiler getCompiler (String language) throws ReflectiveOperationException\n"
			+ "\t{\n" + "\t\treturn getCompiler(-1, -1, -1, language);\n" + "\t}\n" + "\t\n"
			+ "\tpublic static Compiler getCompiler (int ai, int version, int game, String language)\n"
			+ "\t\t\tthrows ReflectiveOperationException\n" + "\t{\n"
			+ "\t\tClass<?> clazz = Class.forName(\"org.pixelgaffer.turnierserver.compile.\" + language + \"Compiler\");\n"
			+ "\t\tCompiler c = (Compiler)clazz\n"
			+ "\t\t\t\t.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE)\n"
			+ "\t\t\t\t.newInstance(ai, version, game);\n" + "\t\treturn c;\n" + "\t}\n" + "\t\n"
			+ "\t/**\n"
			+ "\t * Diese Klasse wird verwendet, um Ausgaben beim Kompilieren einer KI an\n"
			+ "\t * Frontend und FTP weiterzuleiten.\n" + "\t */\n" + "\t@RequiredArgsConstructor\n"
			+ "\tprivate class CompilerDebugWriter extends Writer\n" + "\t{\n" + "\t\t@NonNull\n"
			+ "\t\tprivate Writer ftpFile;\n" + "\t\t@NonNull\n" + "\t\tprivate Backend backend;\n"
			+ "\t\t\n" + "\t\tprivate String buf = \"\";\n" + "\t\t\n" + "\t\t@Override\n"
			+ "\t\tpublic void close () throws IOException\n" + "\t\t{\n" + "\t\t\tflush();\n"
			+ "\t\t\tftpFile.close();\n" + "\t\t}\n" + "\t\t\n" + "\t\t@Override\n"
			+ "\t\tpublic void flush () throws IOException\n" + "\t\t{\n"
			+ "\t\t\tftpFile.flush();\n" + "\t\t\tif (getUuid() != null)\n" + "\t\t\t{\n"
			+ "\t\t\t\tbackend.sendAnswer(new WorkerCommandAnswer(WorkerCommand.COMPILE, WorkerCommandAnswer.MESSAGE,\n"
			+ "\t\t\t\t\t\tgetUuid(), buf));\n" + "\t\t\t}\n" + "\t\t\tbuf = \"\";\n" + "\t\t}\n"
			+ "\t\t\n" + "\t\t@Override\n"
			+ "\t\tpublic void write (char[] buf, int off, int len) throws IOException\n"
			+ "\t\t{\n" + "\t\t\twrite(new String(buf, off, len));\n" + "\t\t}\n" + "\t\t\n"
			+ "\t\t@Override\n" + "\t\tpublic void write (@NonNull String s) throws IOException\n"
			+ "\t\t{\n" + "\t\t\tftpFile.write(s);\n" + "\t\t\tbuf += s;\n" + "\t\t}\n" + "\t}\n"
			+ "\t\n" + "\t@Getter\n" + "\tprivate int ai;\n" + "\t@Getter\n"
			+ "\tprivate int version;\n" + "\t@Getter\n" + "\tprivate int game;\n" + "\t\n"
			+ "\t@Getter\n" + "\t@Setter\n" + "\tprivate UUID uuid;\n" + "\t\n" + "\t@Getter\n"
			+ "\t@Setter(AccessLevel.PROTECTED)\n" + "\tprivate String command;\n" + "\t\n"
			+ "\tpublic CompileResult compileAndUpload (@NonNull Backend backend)\n"
			+ "\t\t\tthrows IOException, InterruptedException, FTPIllegalReplyException, FTPException, FTPDataTransferException,\n"
			+ "\t\t\tFTPAbortedException, FTPListParseException\n" + "\t{\n"
			+ "\t\t// source runterladen\n" + "\t\tif (backend != null)\n"
			+ "\t\t\tbackend.sendAnswer(new WorkerCommandAnswer(WorkerCommand.COMPILE, WorkerCommandAnswer.MESSAGE,\n"
			+ "\t\t\t\t\tgetUuid(), \"> Lade Quelltext herunter ...\\n\"));\n"
			+ "\t\tFile srcdir = DatastoreFtpClient.retrieveAiSource(getAi(), getVersion());\n"
			+ "\t\t\n" + "\t\t// zeugs anlegen\n"
			+ "\t\tFile bindir = Files.createTempDirectory(\"aibin\").toFile();\n"
			+ "\t\tFile output = Files.createTempFile(\"compiler\", \".txt\").toFile();\n"
			+ "\t\tFileWriter ftpFile = new FileWriter(output);\n"
			+ "\t\tWriter w = new CompilerDebugWriter(ftpFile, backend);\n"
			+ "\t\tPrintWriter pw = new PrintWriter(w, true);\n" + "\t\t\n"
			+ "\t\t// properties lesen\n" + "\t\tProperties p = new Properties();\n" + "\t\ttry\n"
			+ "\t\t{\n"
			+ "\t\t\tp.load(new FileInputStream(new File(srcdir, \"settings.prop\")));\n"
			+ "\t\t}\n" + "\t\tcatch (IOException ioe)\n" + "\t\t{\n"
			+ "\t\t\tpw.println(\"> Fehler beim Lesen der Datei settings.prop: \" + ioe);\n"
			+ "\t\t\tpw.close();\n" + "\t\t\tsrcdir.delete();\n" + "\t\t\tbindir.delete();\n"
			+ "\t\t\treturn new CompileResult(false, output);\n" + "\t\t}\n" + "\t\t\n"
			+ "\t\t// compilieren\n"
			+ "\t\tboolean success = compile(srcdir, bindir, p, pw, null);\n" + "\t\t\n"
			+ "\t\t// aufräumen\n" + "\t\tsrcdir.delete();\n" + "\t\t\n" + "\t\tif (success)\n"
			+ "\t\t{\n" + "\t\t\t// packen\n"
			+ "\t\t\tFile archive = Files.createTempFile(\"aibin\", \".tar.bz2\").toFile();\n"
			+ "\t\t\tString files[] = bindir\n"
			+ "\t\t\t\t\t.list( (dir, name) -> !name.equals(\"libraries.txt\") && !name.equals(\"settings.prop\"));\n"
			+ "\t\t\tString cmd[] = new String[files.length + 3];\n" + "\t\t\tcmd[0] = \"tar\";\n"
			+ "\t\t\tcmd[1] = \"cfj\";\n" + "\t\t\tcmd[2] = archive.getAbsolutePath();\n"
			+ "\t\t\tSystem.arraycopy(files, 0, cmd, 3, files.length);\n"
			+ "\t\t\tSystem.out.println(execute(bindir, pw, cmd));\n" + "\t\t\t\n"
			+ "\t\t\t// hochladen\n" + "\t\t\tif (backend != null)\n"
			+ "\t\t\t\tbackend.sendAnswer(new WorkerCommandAnswer(WorkerCommand.COMPILE, WorkerCommandAnswer.MESSAGE,\n"
			+ "\t\t\t\t\t\tgetUuid(), \"> Lade kompilierte KI hoch ...\\n\"));\n"
			+ "\t\t\tDatastoreFtpClient.storeAi(getAi(), getVersion(), new FileInputStream(archive));\n"
			+ "\t\t\t\n" + "\t\t\t// aufräumen\n" + "\t\t\tarchive.delete();\n" + "\t\t}\n"
			+ "\t\t\n" + "\t\t// aufräumen\n" + "\t\tbindir.delete();\n" + "\t\t\n"
			+ "\t\tpw.close();\n" + "\t\treturn new CompileResult(success, output);\n" + "\t}\n"
			+ "\t\n" + "\t/**\n"
			+ "\t * Diese Methode kompiliert den Quelltext einer KI aus srcdir nach bindir.\n"
			+ "\t * In der Datei properties stehen die zur KI gehörenden Eigenschaften wie\n"
			+ "\t * z.B. die Main-Klasse in Java.\n" + "\t */\n"
			+ "\tpublic String compile (File srcdir, File bindir, File properties, LibraryDownloader libs)\n"
			+ "\t\t\tthrows IOException, InterruptedException, CompileFailureException\n" + "\t{\n"
			+ "\t\tif (!bindir.exists() && !bindir.mkdirs())\n"
			+ "\t\t\tthrow new CompileFailureException(\"Konnte das Verzeichnis \" + bindir + \" nicht anlegen!\");\n"
			+ "\t\t\n" + "\t\t// den output in einen String ausgeben\n"
			+ "\t\tStringWriter sw = new StringWriter();\n"
			+ "\t\tPrintWriter output = new PrintWriter(sw);\n" + "\t\t\n"
			+ "\t\tProperties p = new Properties();\n" + "\t\tp.load(new FileReader(properties));\n"
			+ "\t\t\n" + "\t\tboolean success = compile(srcdir, bindir, p, output, libs);\n"
			+ "\t\t\n" + "\t\tif (success)\n" + "\t\t\treturn sw.toString();\n" + "\t\telse\n"
			+ "\t\t\tthrow new CompileFailureException(sw.toString());\n" + "\t}\n" + "\t\n"
			+ "\tpublic abstract boolean compile (File srcdir, File bindir, Properties p, PrintWriter output, LibraryDownloader libs)\n"
			+ "\t\t\tthrows IOException, InterruptedException;\n" + "\t\n"
			+ "\tprotected String relativePath (File absolute, File base)\n" + "\t{\n"
			+ "\t\tPath absolutePath = absolute.toPath();\n"
			+ "\t\tPath basePath = base.toPath();\n"
			+ "\t\tPath relative = basePath.relativize(absolutePath);\n"
			+ "\t\treturn relative.toString();\n" + "\t}\n" + "\t\n"
			+ "\tprotected void copy (File in, File out) throws IOException\n" + "\t{\n"
			+ "\t\tSystem.out.println(\"copy: \" + in + \" → \" + out);\n" + "\t\tout.mkdirs();\n"
			+ "\t\tout.delete();\n" + "\t\tFileInputStream fis = new FileInputStream(in);\n"
			+ "\t\tFileOutputStream fos = new FileOutputStream(out);\n"
			+ "\t\tbyte buf[] = new byte[8192];\n" + "\t\tint read;\n"
			+ "\t\twhile ((read = fis.read(buf)) > 0)\n" + "\t\t\tfos.write(buf, 0, read);\n"
			+ "\t\tfis.close();\n" + "\t\tfos.close();\n" + "\t}\n" + "\t\n"
			+ "\tprotected int execute (File wd, PrintWriter output, String ... command) throws IOException, InterruptedException\n"
			+ "\t{\n" + "\t\t// output.print(wd.getAbsolutePath());\n"
			+ "\t\toutput.print(\"$\");\n" + "\t\tfor (String cmd : command)\n" + "\t\t{\n"
			+ "\t\t\tif (cmd.contains(\" \"))\n" + "\t\t\t\tcmd = \"\\\"\" + cmd + \"\\\"\";\n"
			+ "\t\t\toutput.print(\" \");\n" + "\t\t\toutput.print(cmd);\n" + "\t\t}\n"
			+ "\t\toutput.println();\n" + "\t\t\n"
			+ "\t\tProcessBuilder pb = new ProcessBuilder(command);\n"
			+ "\t\tFile log = Files.createTempFile(\"compiler\", \".txt\").toFile();\n"
			+ "\t\tpb.redirectErrorStream(true);\n" + "\t\tpb.redirectOutput(log);\n"
			+ "\t\tif (wd != null)\n" + "\t\t\tpb.directory(wd);\n"
			+ "\t\tProcess p = pb.start();\n" + "\t\tint returncode = p.waitFor();\n"
			+ "\t\tReader in = new FileReader(log);\n" + "\t\tchar buf[] = new char[8192];\n"
			+ "\t\tint read;\n" + "\t\twhile ((read = in.read(buf)) > 0)\n"
			+ "\t\t\toutput.write(buf, 0, read);\n" + "\t\tin.close();\n" + "\t\tlog.delete();\n"
			+ "\t\toutput.flush();\n" + "\t\treturn returncode;\n" + "\t}\n" + "}\n";

	@Override public void start (Stage primaryStage) throws Exception
	{
		final CodeArea codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		codeArea.setId("codeArea");

		final Style style = Styles.getStyle("VibrantInk");
		final SyntaxParser parser = new SyntaxParser("/usr/share/katepart5/syntax/java.xml", style);
		codeArea.textProperty().addListener(
				(obs, oldText, newText) -> codeArea.setStyleSpans(0, parser.computeHighlighting(newText)));
		codeArea.replaceText(0, 0, exampleText);

		Scene scene = new Scene(new StackPane(codeArea), 1300, 800);
		scene.getStylesheets().add(parser.generateStylesheet("codeArea").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("KatePartParser");
		primaryStage.show();
	}

	public static void main (String args[])
	{
		launch(args);
	}
}

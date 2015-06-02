import java.io.*;
import java.net.*;
import java.util.regex.*;

public class SandboxFake
{
	static File download (int id, int version) throws Throwable
	{
		System.out.println("Lade KI " + id + "v" + version + " herunter");
		
		Socket mirror = new Socket("::1", 1338);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(mirror.getOutputStream()), true);
		out.println("{'id':" + id + ",'version':" + version + "}");
		InputStream in = mirror.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		while ((c = in.read()) > 0)
		{
			if (c == 0xa)
				break;
			baos.write(c);
		}
		int length = Integer.parseInt(new String(baos.toByteArray()));
		System.out.println(length);
		int written = 0;
		
		File f = new File(System.getProperty("user.home"), ".ai" + id + "v" + version + ".tar.bz2");
		FileOutputStream fos = new FileOutputStream(f);
		
		byte buf[] = new byte[8192]; int read;
		while ((read = in.read(buf)) > 0)
		{
			read = Math.min(read, length - written);
			fos.write(buf, 0, read);
			written += read;
			//System.out.println(written + " / " + length);
			if (written >= length)
				break;
		}
		
		fos.close();
		File dir = new File(System.getProperty("user.home"), ".ai" + id + "v" + version);
		dir.mkdir();
		String cmd = "tar xfj " + f.getAbsolutePath() + " -C " + dir.getAbsolutePath();
		System.out.println(cmd);
		Runtime.getRuntime().exec(cmd);
		return dir;
	}
	
	static String extractFromJson (String json, String name)
	{
		Pattern p = Pattern.compile("\\{.*[\"']" + name + "[\"']*:[\"']?([^\"',]*)[\"']?.*\\}");
		Matcher m = p.matcher(json);
		if (m.matches())
			return m.group(1);
		else
			return null;
	}
	
	public static void main (String args[]) throws Throwable
	{
		Socket worker = new Socket("::1", 1337);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(worker.getOutputStream()), true);
		out.println("S");
		out.println("['Java','Python','Cpp']");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(worker.getInputStream()));
		String line;
		while ((line = in.readLine()) != null)
		{
			System.out.println(line);
			int id = Integer.parseInt(extractFromJson(line, "id"));
			int version = Integer.parseInt(extractFromJson(line, "version"));
			String uuid = extractFromJson(line, "uuid");
			
			File dir = download(id, version);
			File aipropfile = new File(dir, "ai.prop");
			PrintWriter aiprop = new PrintWriter(new FileWriter(aipropfile));
			aiprop.println("turnierserver.worker.host=::1");
			aiprop.println("turnierserver.worker.server.port=1337");
			aiprop.println("turnierserver.worker.server.aichar=A");
			aiprop.println("turnierserver.serializer.compress.worker=false");
			aiprop.println("turnierserver.ai.uuid=" + uuid);
			aiprop.close();
			
			String cmd = "./start.sh " + aipropfile.getAbsolutePath();
			System.out.println(cmd);
			ProcessBuilder pb = new ProcessBuilder("./start.sh", aipropfile.getAbsolutePath());
			pb.redirectErrorStream(false);
			pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			pb.directory(dir);
			pb.start().waitFor();
		}
	}
}

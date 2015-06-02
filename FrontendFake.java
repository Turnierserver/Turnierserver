import java.io.*;
import java.net.*;

public class FrontendFake
{
	public static void main (String args[]) throws Throwable
	{
		Socket socket = new Socket("::1", 1333);
		new Thread(() -> {
			try
			{
				InputStream in = socket.getInputStream();
				byte buf[] = new byte[8]; int read;
				while ((read = in.read(buf)) > 0)
					System.out.write(buf, 0, read);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}).start();
		
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			for (int requestid = 0; true; requestid++)
			{
				
				System.out.print("Frontend> ");
				System.out.flush();
				String line = in.readLine().trim();
				if (line.startsWith("start "))
				{
					System.out.println("{'action':'start','gametype':1,'ais':['" + line.substring(6) + "','" + line.substring(6) + "'],'requestid':" + requestid + "}");
					out.println("{'action':'start','gametype':1,'ais':['" + line.substring(6) + "','" + line.substring(6) + "'],'requestid':" + requestid + "}");
				}
				else if (line.startsWith("compile "))
				{
					System.out.println("{'action':'compile','gametype':1,'id':'" + line.substring(8) + "','requestid':" + requestid + "}");
					out.println("{'action':'compile','gametype':1,'id':'" + line.substring(8) + "','requestid':" + requestid + "}");
				}
				else if (line.equals("quit"))
					System.exit(0);
				else
					System.out.println("Unknown command: " + line);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
}

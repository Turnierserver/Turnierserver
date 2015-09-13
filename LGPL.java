/*
 * LGPL.java
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
import static java.nio.charset.StandardCharsets.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class LGPL
{
	static void handlePython (File f) throws Exception
	{
		System.out.println(f);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), UTF_8));
		StringBuilder sb = new StringBuilder();
		sb.append("####################################################################################\n");
		sb.append("# ").append(f.getName()).append("\n");
		sb.append("#\n");
		sb.append("# Copyright (C) ").append(new SimpleDateFormat("yyyy").format(new Date())).append(" Pixelgaffer\n");
		sb.append("#\n");
		sb.append("# This work is free software; you can redistribute it and/or modify it\n");
		sb.append("# under the terms of the GNU Lesser General Public License as published by the\n");
		sb.append("# Free Software Foundation; either version 2 of the License, or any later\n");
		sb.append("# version.\n");
		sb.append("#\n");
		sb.append("# This work is distributed in the hope that it will be useful, but without\n");
		sb.append("# any warranty; without even the implied warranty of merchantability or\n");
		sb.append("# fitness for a particular purpose. See version 2 and version 3 of the\n");
		sb.append("# GNU Lesser General Public License for more details.\n");
		sb.append("#\n");
		sb.append("# You should have received a copy of the GNU Lesser General Public License\n");
		sb.append("# along with this program.  If not, see <http://www.gnu.org/licenses/>.\n");
		sb.append("####################################################################################\n");
		boolean license = false, body = false;
		String line;
		while ((line = in.readLine()) != null)
		{
			if (!body && line.trim().startsWith("#"))
			{
				license = true;
			}
			else if (!body && license && !line.trim().startsWith("#"))
			{
				license = false;
			}
			else if (body || !license)
			{
				body = true;
				sb.append(line).append("\n");
			}
		}
		in.close();
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), UTF_8));
		out.print(sb.toString());
		out.close();
	}
	
	static void handleFile (File f) throws Exception
	{
		if (f.getName().endsWith(".py"))
			handlePython(f);
		if (!f.getName().endsWith(".java") && !f.getName().endsWith(".cpp") && !f.getName().endsWith(".h"))
			return;
		System.out.println(f);
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f), UTF_8));
		StringBuilder sb = new StringBuilder();
		sb.append("/*\n");
		sb.append(" * ").append(f.getName()).append("\n");
		sb.append(" *\n");
		sb.append(" * Copyright (C) ").append(new SimpleDateFormat("yyyy").format(new Date())).append(" Pixelgaffer\n");
		sb.append(" *\n");
		sb.append(" * This work is free software; you can redistribute it and/or modify it\n");
		sb.append(" * under the terms of the GNU Lesser General Public License as published by the\n");
		sb.append(" * Free Software Foundation; either version 2 of the License, or any later\n");
		sb.append(" * version.\n");
		sb.append(" *\n");
		sb.append(" * This work is distributed in the hope that it will be useful, but without\n");
		sb.append(" * any warranty; without even the implied warranty of merchantability or\n");
		sb.append(" * fitness for a particular purpose. See version 2 and version 3 of the\n");
		sb.append(" * GNU Lesser General Public License for more details.\n");
		sb.append(" *\n");
		sb.append(" * You should have received a copy of the GNU Lesser General Public License\n");
		sb.append(" * along with this program.  If not, see <http://www.gnu.org/licenses/>.\n");
		sb.append(" */\n");
		boolean license = false, body = false;
		String line;
		while ((line = in.readLine()) != null)
		{
			if (!body && line.trim().equals("/*"))
			{
				license = true;
			}
			else if (!body && line.trim().equals("*/"))
			{
				license = false;
			}
			else if (body || !license)
			{
				body = true;
				sb.append(line).append("\n");
			}
		}
		in.close();
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), UTF_8));
		out.print(sb.toString());
		out.close();
	}
	
	static void handleTree (File dir) throws Exception
	{
		if (!dir.isDirectory())
		{
			handleFile(dir);
			return;
		}
		for (File f : dir.listFiles())
		{
			if (f.isDirectory())
				handleTree(f);
			else if (f.isFile())
				handleFile(f);
		}
	}
	
	public static void main (String args[]) throws Exception
	{
		if (args.length > 0)
		{
			for (int i = 0; i < args.length; i++)
				handleTree(new File(args[i]));
		}
		else
		{
			BufferedReader in = new BufferedReader(new FileReader(".lgpl-whitelist"));
			String line;
			while ((line = in.readLine()) != null)
			{
				line = line.trim();
				if (!line.isEmpty())
					handleTree(new File(line));
			}
			in.close();
		}
	}
}

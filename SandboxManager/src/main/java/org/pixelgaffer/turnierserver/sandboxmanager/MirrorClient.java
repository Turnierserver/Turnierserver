/*
 * MirrorClient.java
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
package org.pixelgaffer.turnierserver.sandboxmanager;

import static java.lang.Math.min;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.pixelgaffer.turnierserver.PropertyUtils.getIntRequired;
import static org.pixelgaffer.turnierserver.PropertyUtils.getStringRequired;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.UShortSeqHolder;
import org.omg.CORBA.WCharSeqHolder;
import org.pixelgaffer.turnierserver.networking.SHA256;

public class MirrorClient
{
	public static void retrieveAi (int id, int version, String filename)
			throws IOException, NoSuchAlgorithmException
	{
		retrieveLib(Integer.toString(version), Integer.toString(id), filename);
	}
	
	public static void retrieveLib (String language, String lib, String filename)
			throws IOException, NoSuchAlgorithmException
	{
		MirrorClient client = new MirrorClient();
		client.out.println(lib);
		client.out.println(language);
		
		byte salt[] = Base64.getDecoder().decode(client.in.readLine());
		client.out.println(Base64.getEncoder().encodeToString(SHA256.sha256(
				getStringRequired("worker.mirror.password").getBytes(UTF_8), salt,
				getIntRequired("worker.mirror.password.repeats"))));
		
		long size = Long.parseLong(client.in.readLine());
		OutputStream file = new FileOutputStream(filename);
		long written = 0;
		while (written < size)
		{
			int toRead = (int)min(size - written, Integer.MAX_VALUE);
			while (toRead > 0)
			{
				byte buf[] = new byte[min(toRead, 8192)];
				int read = client.in.read(buf);
				System.out.println(read + " (" + written + "/" + size + ")");
				if (read < 0)
				{
					file.close();
					throw new EOFException();
				}
				written += read;
				toRead -= read;
				file.write(buf, 0, read);
			}
		}
		file.close();
	}
	
	private Socket socket;
	private PrintWriter out;
	private DataInputStream in;
	
	private MirrorClient () throws IOException
	{
		socket = new Socket(getStringRequired("worker.host"), getIntRequired("worker.mirror.port"));
		out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
		in = new DataInputStream(socket.getInputStream());
	}
}

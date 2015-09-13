/*
 * GsonGzipParser.java
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
package org.pixelgaffer.turnierserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.GsonBuilder;

public class GsonGzipParser extends GsonParser {

	public GsonGzipParser(GsonBuilder builder) {
		super(builder);
	}

	@Override
	public <E> E parse(byte[] data, Class<E> type) throws IOException {
		return super.parse(uncompress(data), type);
	}

	@Override
	public <E> E parse(byte[] data, Type type) throws IOException {
		return super.parse(uncompress(data), type);
		
	}

	@Override
	public byte[] parse(Object obj, boolean newline) throws IOException {
		return compress(super.parse(obj, newline));
	}
	
	public byte[] compress(byte[] uncompressed) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(uncompressed.length);
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(uncompressed);
		gzip.close();
		return out.toByteArray();
	}
	
	public byte[] uncompress(byte[] compressed) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(compressed);
		GZIPInputStream gzip = new GZIPInputStream(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte buf[] = new byte[9182]; 
		int read; 
		while ((read = gzip.read(buf)) > 0) {
			out.write(buf, 0, read);
		}
		gzip.close();
		
		return out.toByteArray();
	}
	
}

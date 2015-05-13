package org.pixelgaffer.turnierserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.reflect.TypeToken;

public class GsonGzipParser extends GsonParser {

	@Override
	public <E> E parse(byte[] data, Class<E> type) throws IOException {
		return super.parse(uncompress(data), type);
	}

	@Override
	public <E> E parse(byte[] data, TypeToken<E> token) throws IOException {
		return super.parse(uncompress(data), token);
		
	}

	@Override
	public byte[] parse(Object obj) throws IOException {
		return compress(super.parse(obj));
	}
	
	private byte[] compress(byte[] uncompressed) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(uncompressed.length);
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(uncompressed);
		gzip.close();
		return out.toByteArray();
	}
	
	private byte[] uncompress(byte[] compressed) throws IOException {
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

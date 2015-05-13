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
				
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		GZIPInputStream gzip = new GZIPInputStream(in);
		byte[] buffer = new byte[data.length];
		gzip.read(buffer);
		gzip.close();
		
		return super.parse(buffer, type);
	}

	@Override
	public <E> E parse(byte[] data, TypeToken<E> token) throws IOException {
		
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		GZIPInputStream gzip = new GZIPInputStream(in);
		byte[] buffer = new byte[data.length];
		gzip.read(buffer);
		gzip.close();
		
		return super.parse(buffer, token);
		
	}

	@Override
	public byte[] parse(Object obj) throws IOException {
		byte[] data = super.parse(obj);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(super.parse(obj));
		gzip.close();
		
		return out.toByteArray();
	}
	
}

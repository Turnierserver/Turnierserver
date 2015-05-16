package org.pixelgaffer.turnierserver;

import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;

class GsonParser implements Parser {
	
	private GsonBuilder builder;
	
	public GsonParser(GsonBuilder builder) {
		this.builder = builder;
	}

	@Override
	public <E> E parse(byte[] data, Class<E> type) throws IOException {
		return builder.create().fromJson(new String(data, "UTF-8"), type);
	}

	@Override
	public <E> E parse(byte[] data, Type type) throws IOException {
		return builder.create().fromJson(new String(data, "UTF-8"), type);
	}

	@Override
	public byte[] parse(Object obj) throws IOException {
		return builder.create().toJson(obj).getBytes("UTF-8");
	}
	
}

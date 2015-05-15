package org.pixelgaffer.turnierserver;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class GsonParser implements Parser {
	
	private Gson gson;
	
	public GsonParser() {
		gson = new Gson();
	}

	@Override
	public <E> E parse(byte[] data, Class<E> type) throws IOException {
		return gson.fromJson(new String(data, "UTF-8"), type);
	}

	@Override
	public <E> E parse(byte[] data, TypeToken<E> token) throws IOException {
		return gson.fromJson(new String(data, "UTF-8"), token.getType());
	}

	@Override
	public byte[] parse(Object obj) throws IOException {
		return gson.toJson(obj).getBytes("UTF-8");
	}
	
	
}

package org.pixelgaffer.turnierserver;

import java.io.IOException;
import java.lang.reflect.Type;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.google.gson.GsonBuilder;

@RequiredArgsConstructor
class GsonParser implements Parser {
	
	@NonNull
	private GsonBuilder builder;
	
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

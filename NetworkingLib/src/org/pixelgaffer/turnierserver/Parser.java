package org.pixelgaffer.turnierserver;

import com.google.gson.reflect.TypeToken;

public interface Parser {
	
	public <E> E parse(byte[] data, Class<E> type);
	public <E> E parse(byte[] data, TypeToken<E> token);
	
	public byte[] parse(Object obj);
	
}

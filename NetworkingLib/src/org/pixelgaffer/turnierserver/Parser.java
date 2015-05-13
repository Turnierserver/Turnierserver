package org.pixelgaffer.turnierserver;

import java.io.IOException;

import com.google.gson.reflect.TypeToken;

public interface Parser {
	
	public <E> E parse(byte[] data, Class<E> type) throws IOException;
	public <E> E parse(byte[] data, TypeToken<E> token) throws IOException;
	
	public byte[] parse(Object obj) throws IOException;
	
}

package org.pixelgaffer.turnierserver;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.lang.reflect.Type;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.google.gson.GsonBuilder;

@RequiredArgsConstructor
class GsonParser implements Parser
{
	@NonNull
	private GsonBuilder builder;
	
	@Override
	public <E> E parse (byte[] data, Class<E> type) throws IOException
	{
		return builder.create().fromJson(new String(data, UTF_8), type);
	}
	
	@Override
	public <E> E parse (byte[] data, Type type) throws IOException
	{
		return builder.create().fromJson(new String(data, UTF_8), type);
	}
	
	@Override
	public byte[] parse (Object obj, boolean newline) throws IOException
	{
		return (builder.create().toJson(obj) + "\n").getBytes(UTF_8);
	}
}

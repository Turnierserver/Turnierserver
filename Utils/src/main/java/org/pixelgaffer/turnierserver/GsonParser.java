/*
 * GsonParser.java
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

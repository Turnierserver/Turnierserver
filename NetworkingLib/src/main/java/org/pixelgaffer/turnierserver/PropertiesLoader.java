package org.pixelgaffer.turnierserver;

import java.io.FileInputStream;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertiesLoader
{
	@SneakyThrows
	public static Properties loadProperties (String filename)
	{
		Properties p = new Properties(System.getProperties());
		p.load(new FileInputStream(filename));
		System.setProperties(p);
		return p;
	}
}

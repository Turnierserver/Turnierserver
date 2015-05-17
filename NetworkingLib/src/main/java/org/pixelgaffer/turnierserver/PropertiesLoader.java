package org.pixelgaffer.turnierserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertiesLoader
{

	public static Properties loadProperties (String filename) throws IOException
	{
		Properties p = new Properties(System.getProperties());
		p.load(new FileInputStream(filename));
		System.setProperties(p);
		return p;
	}
	
	public static boolean getBoolean(String key, boolean def) {
		String prop = System.getProperty(key);
		if(prop == null) {
			return def;
		}
		try {
			return Boolean.parseBoolean(prop);
		} catch(Exception e) {
			return def;
		}
	}
	
	public static boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public static int getInt(String key, int def) {
		String prop = System.getProperty(key);
		if(prop == null) {
			return def;
		}
		try {
			return Integer.parseInt(prop);
		} catch(Exception e) {
			return def;
		}
	}
	
	public static int getInt(String key) {
		return getInt(key, 0);
	}
	
	public static double getDouble(String key, double def) {
		String prop = System.getProperty(key);
		if(prop == null) {
			return def;
		}
		try {
			return Double.parseDouble(prop);
		} catch(Exception e) {
			return def;
		}
	}
	
	public static double getDouble(String key) {
		return getDouble(key, 0);
	}
	
	public static String getString(String key, String def) {
		String prop = System.getProperty(key);
		if(prop == null) {
			return def;
		}
		return prop;
	}
	
	public static String getString(String key) {
		return getString(key, "");
	}
	
}

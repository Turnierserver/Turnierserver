package org.pixelgaffer.turnierserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertyUtils
{
	private static final String TURNIERSERVER_BASE = "turnierserver.";
	private static final String BACKEND_BASE = TURNIERSERVER_BASE + "backend.";
	private static final String WORKER_BASE = TURNIERSERVER_BASE + "worker.";
	private static final String WORKER_SERVER_BASE = WORKER_BASE + "server.";
	private static final String WORKER_MIRROR_BASE = WORKER_BASE + "mirror.";
	private static final String DATASTORE_BASE = TURNIERSERVER_BASE + "datastore.";
	private static final String SERIALIZER_BASE = TURNIERSERVER_BASE + "serializer.";
	private static final String AI_BASE = TURNIERSERVER_BASE + "ai.";
	private static final String BACKEND_WORKER_SERVER_BASE = BACKEND_BASE + "workerserver.";
	private static final String BACKEND_FRONTEND_SERVER_BASE = BACKEND_BASE + "frontendserver.";
	
	public static final String BACKEND_HOST = BACKEND_BASE + "host";
	public static final String BACKEND_FRONTEND_SERVER_PORT = BACKEND_FRONTEND_SERVER_BASE + "port";
	public static final String BACKEND_WORKER_SERVER_PORT = BACKEND_WORKER_SERVER_BASE + "port";
	public static final String BACKEND_WORKER_SERVER_MAX_CLIENTS = BACKEND_WORKER_SERVER_BASE + "maxClients";
	public static final String WORKER_HOST = WORKER_BASE + "host";
	public static final String WORKER_SERVER_PORT = WORKER_SERVER_BASE + "port";
	public static final String WORKER_SERVER_MAX_CLIENTS = WORKER_SERVER_BASE + "maxClients";
	public static final String WORKER_SERVER_AICHAR = WORKER_SERVER_BASE + "aichar";
	public static final String WORKER_MIRROR_PORT = WORKER_MIRROR_BASE + "port";
	public static final String WORKER_MIRROR_PASSWORD = WORKER_MIRROR_BASE + "password";
	public static final String WORKER_MIRROR_PASSWORD_REPEATS = WORKER_MIRROR_BASE + "passwordRepeats";
	public static final String WORKER_MIRROR_SALT_LENGTH = WORKER_MIRROR_BASE + "saltLength";
	public static final String DATASTORE_HOST = DATASTORE_BASE + "host";
	public static final String DATASTORE_PORT = DATASTORE_BASE + "port";
	public static final String DATASTORE_USERNAME = DATASTORE_BASE + "username";
	public static final String DATASTORE_PASSWORD = DATASTORE_BASE + "password";
	public static final String SERIALIZER_COMPRESSING = SERIALIZER_BASE + "compress";
	public static final String SERIALIZER_WORKER_COMPRESSING = SERIALIZER_COMPRESSING + ".worker";
	public static final String SERIALIZER_SANDBOX_COMPRESSING = SERIALIZER_COMPRESSING + ".sandbox";
	public static final String SERIALIZER_FRONTEND_COMPRESSING = SERIALIZER_COMPRESSING + ".frontend";
	public static final String AI_UUID = AI_BASE + "uuid";
	
	public static final String FTP_CONNECTIONS = TURNIERSERVER_BASE + "ftpconnections";
	public static final String RECON_IVAL = TURNIERSERVER_BASE + ".reconnectionInterval";
	
	public static Properties loadProperties (String filename) throws IOException
	{
		Properties p = new Properties(System.getProperties());
		p.load(new FileInputStream(filename));
		System.setProperties(p);
		return p;
	}
	
	
	public static boolean getBooleanRequired (String key)
	{
		return Boolean.parseBoolean(System.getProperty(key));
	}
	
	public static boolean getBoolean (String key, boolean def)
	{
		String prop = System.getProperty(key);
		if (prop == null)
		{
			return def;
		}
		try
		{
			return Boolean.parseBoolean(prop);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public static boolean getBoolean (String key)
	{
		return getBoolean(key, false);
	}
	
	public static int getIntRequired (String key)
	{
		return Integer.parseInt(System.getProperty(key));
	}
	
	public static int getInt (String key, int def)
	{
		String prop = System.getProperty(key);
		if (prop == null)
		{
			return def;
		}
		try
		{
			return Integer.parseInt(prop);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public static int getInt (String key)
	{
		return getInt(key, 0);
	}
	
	public static double getDoubleRequired (String key)
	{
		return Double.parseDouble(System.getProperty(key));
	}
	
	public static double getDouble (String key, double def)
	{
		String prop = System.getProperty(key);
		if (prop == null)
		{
			return def;
		}
		try
		{
			return Double.parseDouble(prop);
		}
		catch (Exception e)
		{
			return def;
		}
	}
	
	public static double getDouble (String key)
	{
		return getDouble(key, 0);
	}
	
	public static String getStringRequired (String key)
	{
		return System.getProperty(key).toString();
	}
	
	public static String getString (String key, String def)
	{
		String prop = System.getProperty(key);
		if (prop == null)
		{
			return def;
		}
		return prop;
	}
	
	public static String getString (String key)
	{
		return getString(key, "");
	}
	
}

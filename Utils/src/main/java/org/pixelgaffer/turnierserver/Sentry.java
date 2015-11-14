/*
 * Sentry.java
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

import java.lang.Thread.UncaughtExceptionHandler;
import net.kencochrane.raven.RavenFactory;
import net.kencochrane.raven.log4j.SentryAppender;

public final class Sentry implements UncaughtExceptionHandler
{
	private static org.apache.log4j.Logger log4jLogger = null;
	private static org.pixelgaffer.turnierserver.Logger logger = new org.pixelgaffer.turnierserver.Logger();
	
	private static void init()
	{
		if (log4jLogger != null)
			return;
		String mainclass = System.getProperty("sun.java.command").split("\\s+")[0];
		if (mainclass.contains("."))
			mainclass = mainclass.substring(mainclass.lastIndexOf('.') + 1);
		log4jLogger = org.apache.log4j.Logger.getLogger(mainclass);
		String dsn = PropertyUtils.getString("sentry.dsn");
		if (dsn != null)
		{
			SentryAppender appender = new SentryAppender(RavenFactory.ravenInstance(dsn));
            log4jLogger.addAppender(appender);
		}
		else
			logger.warning("No sentry.dsn configuration, won't use sentry");
	}
	
	public static <T extends Throwable> T log (T t)
	{
		init();
		log4jLogger.error(t);
		return t;
	}
	
	public static String log (String s)
	{
		init();
		log4jLogger.error(s);
		return s;
	}

	@Override
	public void uncaughtException (Thread thread, Throwable t)
	{
		logger.critical("Thread " + thread + " crashed: " + t);
		log(t).printStackTrace();
	}
}

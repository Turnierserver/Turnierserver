/*
 * Airbrake.java
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import airbrake.AirbrakeAppender;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Airbrake
{
	private static Logger logger = Logger.getLogger("airbrake");
	
	static
	{
		String apiKey = PropertyUtils.getString("turnierserver.airbrake.key");
		if (apiKey != null)
		{
			AirbrakeAppender appender = new AirbrakeAppender("29f237f4ba8490288d2fd567679b3fc5");
            try
			{
				appender.setEnv(InetAddress.getLocalHost().getHostName());
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
				appender.setEnv("unknown");
			}
            appender.setEnabled(true);
            logger.addAppender(appender);
		}
	}
	
	public static <T extends Throwable> T log (T t)
	{
		logger.error(t);
		return t;
	}
	
	public static String log (String s)
	{
		logger.error(s);
		return s;
	}
	
}

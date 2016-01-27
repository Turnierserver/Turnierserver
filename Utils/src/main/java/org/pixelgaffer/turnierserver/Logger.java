/*
 * Logger.java
 *
 * Copyright (C) 2016 Pixelgaffer
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger
{
	enum Category
	{
		INFO,
		DEBUG,
		WARNING,
		CRITICAL
	}
	
	private static final DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS z");
	
	public void info (Object o)
	{
		log(o, Category.INFO);
	}
	
	public void debug (Object o)
	{
		log(o, Category.DEBUG);
	}
	
	public void warning (Object o)
	{
		log(o, Category.WARNING);
	}
	
	public void critical (Object o)
	{
		log(o, Category.CRITICAL);
	}
	
	protected synchronized void log (Object o, Category category)
	{
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stacktrace[3];
		String classname = caller.getClassName();
		classname = classname.lastIndexOf('.') > 0 ? classname.substring(classname.lastIndexOf('.') + 1) : classname;
		boolean escapeCodes = System.console() != null;
		if (escapeCodes)
			System.err.print("\033[36m");
		System.err.print("[" + df.format(new Date()) + "] ");
		if (escapeCodes)
		{
			System.err.print("\033[0m");
			switch (category)
			{
				case INFO:
					System.err.print("\033[1m    INFO ");
					break;
				case DEBUG:
					System.err.print("\033[1m   DEBUG ");
					break;
				case WARNING:
					System.err.print("\033[1;33m WARNING ");
					break;
				case CRITICAL:
					System.err.print("\033[1;31mCRITICAL ");
					break;
			}
			System.err.print("\033[0min \033[1;32m" + classname + "::" + caller.getMethodName() + "\033[0m \033[32m("
					+ caller.getFileName() + ":" + caller.getLineNumber() + ")\033[0m");
		}
		else
		{
			switch (category)
			{
				case INFO:
					System.err.print("    INFO ");
					break;
				case DEBUG:
					System.err.print("   DEBUG ");
					break;
				case WARNING:
					System.err.print(" WARNING ");
					break;
				case CRITICAL:
					System.err.print("CRITICAL ");
					break;
			}
			System.err.print("in " + classname + "::" + caller.getMethodName() + " (" + caller.getFileName() + ":"
					+ caller.getLineNumber() + ")");
		}
		System.err.println(": " + o.toString());
	}
	
	public synchronized void todo (String what)
	{
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stacktrace[2];
		String classname = caller.getClassName();
		classname = classname.lastIndexOf('.') > 0 ? classname.substring(classname.lastIndexOf('.') + 1) : classname;
		boolean escapeCodes = System.console() != null;
		if (escapeCodes)
			System.err.println("\033[1;33mTODO\033[0m in \033[1;32m" + classname + "::" + caller.getMethodName()
					+ "\033[0m \033[32m(" + caller.getFileName() + ":" + caller.getLineNumber() + ")\033[0m: " + what);
		else
			System.err.println("TODO in " + classname + "::" + caller.getMethodName() + " (" + caller.getFileName()
					+ ":" + caller.getLineNumber() + "): " + what);
	}
	
	public synchronized void stacktrace (String what)
	{
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		boolean escapeCodes = System.console() != null;
		if (escapeCodes)
			System.err.println("\033[1;33mSTACKTRACE\033[0m " + what);
		else
			System.err.println("STACKTRACE " + what);
		for (int i = 2; i < stacktrace.length; i++)
		{
			if (escapeCodes)
			{
				String classname = stacktrace[i].getClassName();
				String pkgname = classname.substring(0, classname.lastIndexOf('.'));
				classname = classname.substring(pkgname.length() + 1);
				System.err.println("  - " + pkgname + ".\033[1;32m" + classname + "::" + stacktrace[i].getMethodName()
						+ "\033[0m (\033[32m" + stacktrace[i].getFileName() + ":" + stacktrace[i].getLineNumber() + "\033[0m)");
			}
			else
				System.err.println("  - " + stacktrace[i].getClassName() + "::" + stacktrace[i].getMethodName()
						+ " (" + stacktrace[i].getFileName() + ":" + stacktrace[i].getLineNumber() + ")");
		}
	}
}

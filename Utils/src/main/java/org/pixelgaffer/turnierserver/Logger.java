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
	
	public void log (Object o, Category category)
	{
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
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
					System.err.print("\033[1mINFO ");
					break;
				case DEBUG:
					System.err.print("\033[1mDEBUG ");
					break;
				case WARNING:
					System.err.print("\033[1;33mWARNING ");
					break;
				case CRITICAL:
					System.err.print("\033[1;31mCRITICAL ");
					break;
			}
			System.err.print("\033[0min \033[1;32m" + stacktrace[3].getMethodName() + "\033[0m \033[32m("
					+ stacktrace[3].getFileName() + ":" + stacktrace[3].getLineNumber() + ")\033[0m");
		}
		else
		{
			switch (category)
			{
				case INFO:
					System.err.print("INFO     ");
					break;
				case DEBUG:
					System.err.print("DEBUG    ");
					break;
				case WARNING:
					System.err.print("WARNING  ");
					break;
				case CRITICAL:
					System.err.print("CRITICAL ");
					break;
			}
			System.err.print("in " + stacktrace[3].getMethodName() + " (" + stacktrace[3].getFileName() + ":"
					+ stacktrace[3].getLineNumber() + ")");
		}
		System.err.println(": " + o.toString());
	}
}

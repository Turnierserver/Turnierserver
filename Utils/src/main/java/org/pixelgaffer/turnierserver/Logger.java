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
	
	protected void log (Object o, Category category)
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
			System.err.print("\033[0min \033[1;32m" + classname + "::" + caller.getMethodName() + "\033[0m \033[32m("
					+ caller.getFileName() + ":" + caller.getLineNumber() + ")\033[0m");
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
			System.err.print("in " + classname + "::" + caller.getMethodName() + " (" + caller.getFileName() + ":"
					+ caller.getLineNumber() + ")");
		}
		System.err.println(": " + o.toString());
	}
	
	public void todo (String what)
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
}

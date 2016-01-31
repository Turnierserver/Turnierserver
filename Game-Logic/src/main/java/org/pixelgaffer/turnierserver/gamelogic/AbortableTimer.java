package org.pixelgaffer.turnierserver.gamelogic;

public class AbortableTimer
{
	private boolean aborted = false;
	private Thread timerThread;
	private int millis;
	private long start;
	private Object lock = new Object();
	private Runnable run;
	
	public AbortableTimer (int millis, Runnable run)
	{
		this.millis = millis;
		this.run = run;
	}
	
	private void timer ()
	{
		synchronized (lock)
		{
			try
			{
				while (start + millis > System.currentTimeMillis())
				{
					long wait = millis - System.currentTimeMillis() + start;
					System.out.println("warte " + wait);
					lock.wait(wait);
					System.out.println("habe " + wait + " gewartet");
				}
				System.out.println("bin fertig mit warten");
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				aborted = true;
			}
		}
		if (!aborted)
			run.run();
		else
			System.out.println("wurde geabortet");
		timerThread = null;
	}
	
	public void abort ()
	{
		aborted = true;
		synchronized (lock)
		{
			lock.notifyAll();
		}
	}
	
	public void restart ()
	{
		start = System.currentTimeMillis();
		aborted = false;
		if (timerThread == null)
		{
			timerThread = new Thread(this::timer);
			timerThread.start();
		}
	}
	
}

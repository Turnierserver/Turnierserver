package org.pixelgaffer.turnierserver.gamelogic;

public class AbortableTimer {
	
	private boolean aborted = false;
	private Thread timerThread;
	private int millis;
	private Object lock = new Object();
	private Runnable run;
	
	public AbortableTimer(int millis, Runnable run) {
		this.millis = millis;
		this.run = run;
	}
	
	private void timer() {
		synchronized (lock) {
			try {
				lock.wait(millis);
			} catch (InterruptedException e) {
				e.printStackTrace();
				aborted = true;
			}
		}
		if(!aborted) {
			run.run();
		}
		timerThread = null;
	}
	
	public void abort() {
		aborted = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}
	
	public void restart() {
		if(timerThread != null) {
			abort();
		}
		timerThread = new Thread(this::timer);
		timerThread.start();
	}
	
}

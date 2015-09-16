package org.pixelgaffer.turnierserver.ailibrary;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import org.pixelgaffer.turnierserver.Logger;
import org.pixelgaffer.turnierserver.PropertyUtils;

/**
 * @param <R>
 *            Die Antwort der Spiellogik
 * @param <U>
 *            Die Antwort der Ki
 */
public abstract class Ai implements Runnable {
	
	public static Logger logger = new Logger();
	
	/**
	 * Die Connection zum Worker
	 */
	private Socket con;
	/**
	 * Der Printwriter der Connection
	 */
	private BufferedOutputStream out;
	/**
	 * Der BufferedReader der Connection
	 */
	private BufferedReader in;
	
	/**
	 * Der kummulierte String von System.out
	 */
	protected StringBuilder output = new StringBuilder();
		
	public Ai(String[] args) {
		try {
			PropertyUtils.loadProperties(args.length > 0 ? args[0] : "ai.prop");
			logger.info("Connecting to " + PropertyUtils.getStringRequired(PropertyUtils.WORKER_HOST) + ":" + PropertyUtils.getIntRequired(PropertyUtils.WORKER_SERVER_PORT));
			con = new Socket(PropertyUtils.getStringRequired(PropertyUtils.WORKER_HOST), PropertyUtils.getIntRequired(PropertyUtils.WORKER_SERVER_PORT));
			out = new BufferedOutputStream(con.getOutputStream());
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			out.write((PropertyUtils.getStringRequired(PropertyUtils.WORKER_SERVER_AICHAR) + PropertyUtils.getStringRequired(PropertyUtils.AI_UUID) + "\n").getBytes(UTF_8));
			out.flush();
			
			boolean debug = true;
			logger.info("Debugging is " + (debug ? "enabled" : "disabled"));
			PrintStream stdout = System.out;
			System.setOut(new PrintStream(new OutputStream() {
				public void write(int b) throws IOException {
					output.append((char) b);
					if (debug)
						stdout.write(b);
				}
			}));
			PrintStream stderr = System.err;
			System.setErr(new PrintStream(new OutputStream() {
				public void write(int b) throws IOException {
					output.append((char) b);
					if (debug)
						stderr.write(b);
				}
			}));
		} catch (Exception e) {
			crash(e);
		}
	}
	
	/**
	 * Wird aufgerufen, sobald der Server ein Gamestate-Update sendet
	 * 
	 * @param answer Die Antwort vom Server
	 * 
	 * @return Die Antwort an den Server, null wenn keine gesendet werden soll
	 *         (der Server wartet bei rundenbasierten Spielen trotzdem auf eine
	 *         Antwort)
	 */
	protected abstract String update(String answer);
	
	public final void run() {
		
		try {
			while (true) {
				logger.debug("in mainloop");
				if (con.isClosed()) {
					System.exit(0);
				}
				String line = in.readLine();
				if (line == null) System.exit(0);
				logger.debug("erhalten:");
				logger.debug(line);
				logger.debug("==================================================");
				String response = update(line);
				logger.debug("response:");
				logger.debug(response);
				logger.debug("==================================================");
				response += ":" + output.toString().substring(0, Math.min(1000, output.length())).replace("\\", "\\\\").replace("\n", "\\n");
				output.delete(0, output.length());
				if (response != null) {
					send(response);
				}
			}
		} catch (Exception e) {
			crash(e);
		}
	}
	
	/**
	 * ACHTUNG: Mit dieser Methode gibt die KI automatisch auf
	 */
	public final void surrender() {
		send("SURRENDER");
	}
	
	/**
	 * ACHTUNG: Mit dieser Methode signalisiert man einen Crash -> Die KI verliert
	 */
	public final void crash(Throwable t) {
		t.printStackTrace();
		crash(t.getMessage() == null ? t.toString() : t.getMessage());
	}
	
	/**
	 * ACHTUNG: Mit dieser Methode signalisiert man einen Crash -> Die KI verliert
	 */
	public final void crash(String reason) {
		try {
			out.write(("CRASH " + reason + "\n").getBytes(UTF_8));
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public final void send(String s) {
		try {
			out.write((s + "\n").getBytes(UTF_8));
			out.flush();
		} catch (Exception e) {
			crash(e);
		}
	}
	
	/**
	 * Muss in der Main-Methode aufgerufen werden, damit die KI sich zum Worker verbinden kann
	 */
	public final void start() {
		new Thread(this).start();
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				crash(e);
			}
		}
	}
	
}

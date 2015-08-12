package org.pixelgaffer.turnierserver.ailibrary;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import org.pixelgaffer.turnierserver.Logger;
import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.PropertyUtils;

import com.google.gson.reflect.TypeToken;

/**
 * @param <E>
 *            Der GameState
 * @param <R>
 *            Die Antwort der Spiellogik
 */
public abstract class Ai<E, R> implements Runnable {
	
	public static Logger logger = new Logger();
	
	/**
	 * Die Connection zum Worker
	 */
	private Socket con;
	/**
	 * Der Printwriter der Connection
	 */
	private PrintWriter out;
	/**
	 * Der BufferedReader der Connection
	 */
	private BufferedReader in;
	
	/**
	 * Der kummulierte String von System.out
	 */
	protected StringBuilder output = new StringBuilder();
	/**
	 * Der momentane Gamestate des Servers
	 */
	protected Map<String, String> gamestate;
	
	private TypeToken<R> token;
	
	public Ai(TypeToken<R> token, String[] args) {
		this.token = token;
		try {
			PropertyUtils.loadProperties(args.length > 0 ? args[0] : "ai.prop");
			logger.info("Connecting to " + PropertyUtils.getStringRequired(PropertyUtils.WORKER_HOST) + ":" + PropertyUtils.getIntRequired(PropertyUtils.WORKER_SERVER_PORT));
			con = new Socket(PropertyUtils.getStringRequired(PropertyUtils.WORKER_HOST), PropertyUtils.getIntRequired(PropertyUtils.WORKER_SERVER_PORT));
			out = new PrintWriter(con.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			out.println(PropertyUtils.getStringRequired(PropertyUtils.WORKER_SERVER_AICHAR) + PropertyUtils.getStringRequired(PropertyUtils.AI_UUID));
			System.setOut(new PrintStream(new OutputStream() {
				
				public void write(int b) throws IOException {
					output.append((char) b);
				}
			}));
		} catch (Exception e) {
			crash(e);
		}
	}
	
	/**
	 * Wird aufgerufen, sobald der Server ein Gamestate-Update sendet
	 * 
	 * @param state
	 *            Der Gamestate
	 * @return Die Antwort an den Server, null wenn keine gesendet werden soll
	 *         (der Server wartet bei rundenbasierten Spielen trotzdem auf eine
	 *         Antwort)
	 */
	protected abstract Object update(E state);
	
	/**
	 * Gibt den momentanen Spielzustand zurück
	 * 
	 * @return Der momentane Spielzustand
	 */
	protected abstract E getState(R change);
	
	public final void run() {
		
		try {
			while (true) {
				if (con.isClosed()) {
					System.exit(0);
				}
				String line = in.readLine();
				if (line == null) throw new EOFException();
				logger.info("JSON erhalten: " + line);
				logger.debug("um " + System.currentTimeMillis());
				R updates = Parsers.getWorker().parse(Parsers.escape(line.getBytes("UTF-8")), token.getType());
				logger.info("Geparsed zu: " + updates);
				Object response = update(getState(updates));
				logger.info("Sender response:" + response);
				if (response != null) {
					send(response);
				}
				logger.debug("um " + System.currentTimeMillis());
			}
		} catch (Exception e) {
			crash(e);
		}
	}
	
	/**
	 * Sendet ein Objekt. Wenn ein Objekt nicht geparsed werden kann, oder wenn bei rundenbasierten Spielen mehrere Objekte pro Runde gesendet werden, verliert die ki automatisch
	 * 
	 * @param o
	 */
	protected final void send(Object o) {
		try {
			Parsers.escape(Parsers.getWorker().parse(o), con.getOutputStream());
			con.getOutputStream().write(0xa);
		} catch (Exception e) {
			crash(e);
		}
	}
	
	/**
	 * ACHTUNG: Mit dieser Methode gibt die KI automatisch auf
	 */
	public final void surrender() {
		out.println("SURRENDER");
		System.exit(0);
	}
	
	/**
	 * ACHTUNG: Mit dieser Methode signalisiert man einen Crash -> Die KI verliert
	 */
	public final void crash(Throwable t) {
		out.println("CRASH " + t.getMessage());
		System.exit(1);
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
	
	/**
	 * Tolle Extension-Methode für xtend
	 * 
	 * @param builder
	 *            Der Builder, der geleert werden soll
	 */
	protected void clear(StringBuilder builder) {
		builder.delete(0, builder.length());
	}
	
}

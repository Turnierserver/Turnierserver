package org.pixelgaffer.turnierserver.ailibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.PropertyUtils;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.GameState;

import com.google.gson.reflect.TypeToken;

/**
 * @param <E> Der GameState
 * @param <R> Die Antwort der Spiellogik
 */
public abstract class Ai<E extends GameState<R, ?>, R> implements Runnable {
	
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
	protected StringBuilder output;
	/**
	 * Der momentane Gamestate des Servers
	 */
	protected Map<String, String> gamestate;
	
	private TypeToken<R> token = new TypeToken<R>() {};
	
	public Ai() {
		try {
			PropertyUtils.loadProperties("ai.prop");
			con = new Socket("localhost", Integer.parseInt(System.getProperty("ai.con.port")));
			out = new PrintWriter(con.getOutputStream());
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			out.println("A" + System.getProperty("ai.con.id"));
			System.setOut(new PrintStream(new OutputStream() {
				public void write(int b) throws IOException {
					output.append((char) b);
				}
			}));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Wird aufgerufen, sobald der Server ein Gamestate-Update sendet
	 * 
	 * @param state Der Gamestate
	 * @return Die Antwort an den Server, null wenn keine gesendet werden soll (der Server wartet bei rundenbasierten Spielen trotzdem auf eine Antwort)
	 */
	protected abstract Object update(E state);
	/**
	 * Gibt den momentanen Spielzustand zurück
	 * 
	 * @return Der momentane Spielzustand
	 */
	protected abstract E getState(R change);
	
	@Override
	public final void run() {
		
		try {
			while(true) {
				if(con.isClosed()) {
					System.exit(0);
				}
				String line = in.readLine();
				R updates = Parsers.getWorker().parse(line.getBytes("UTF-8"), token.getType());
				Object response = update(getState(updates));
				if(response != null) {
					send(response);
				}
			}
		}
		catch(IOException e) {
			System.exit(0);
		}
	}
	
	/**
	 * Sendet ein Objekt. Wenn ein Objekt nicht geparsed werden kann, oder wenn bei rundenbasierten Spielen mehrere Objekte pro Runde gesendet werden, verliert die ki automatisch
	 * @param o
	 */
	protected final void send(Object o) {
		try {
			Parsers.escape(Parsers.getWorker().parse(o), con.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
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
	 * Muss in der Main-Methode aufgerufen werden, damit die KI sich zum Worker verbinden kann
	 */
	public final void start() {
		new Thread(this).start();
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				surrender();
			}
		}
	}
	
}

package org.pixelgaffer.turnierserver.ailibrary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.msgpack.MessagePack;

public abstract class Ai<E, R> implements Runnable {
	
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
	 * Die MessagePack Instanz, die zum parsen der Objekte verwendet wird
	 */
	private MessagePack msgpack;
	
	/**
	 * Der kummulierte String von System.out 
	 */
	protected StringBuilder output;
	/**
	 * Der momentane Gamestate des Servers
	 */
	protected Map<String, String> gamestate;
	
	public Ai() {
		try {
			con = new Socket("localhost", 1337);
			out = new PrintWriter(con.getOutputStream());
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
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
	protected abstract R update(E state);
	/**
	 * Gibt den momentanen Spielzustand zurück
	 * 
	 * @return Der momentane Spielzustand
	 */
	protected abstract E getState();
	
	@SuppressWarnings("unchecked")
	@Override
	public final void run() {
		
		try {
			while(true) {
				if(con.isClosed()) {
					System.exit(0);
				}
				String line = in.readLine();
				Map<String, String> updates = msgpack.read(line.getBytes("UTF-8"), HashMap.class);
				for(Entry<String, String> update : updates.entrySet()) {
					gamestate.put(update.getKey(), update.getValue());
				}
				R response = update(getState());
				if(response != null) {
					out.println(new String(msgpack.write(response), "UTF-8"));
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
			out.println(new String(msgpack.write(o), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ACHTUNG: Mit dieser Methode gibt die KI automatisch auf
	 */
	public final void surrender() {
		out.println("SURRENDER");
	}
	
	/**
	 * Muss in der Main-Methode aufgerufen werden, damit die KI sich zum Worker verbinden kann
	 */
	public final void start() {
		new Thread(this).start();
	}
	
}

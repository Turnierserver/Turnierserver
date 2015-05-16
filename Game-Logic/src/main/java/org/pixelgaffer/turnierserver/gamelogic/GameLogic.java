package org.pixelgaffer.turnierserver.gamelogic;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.pixelgaffer.turnierserver.Parsers;

public abstract class GameLogic<E extends AiObject, R> {
	
	/**
	 * Das Spiel, welches von dieser GameLogic geleitet wird
	 */
	protected Game game;
	
	/**
	 * Die Art der Antwort
	 */
	private Class<R> responseType;
	/**
	 * Der Gamstate
	 */
	private Map<String, String> gamestate;
	/**
	 * Die Sachen, welche sich im Gamestate verändert haben
	 */
	private Map<String, String> changed;
	
	/**
	 * Der Konstruktor, MUSS verwendet werden
	 * 
	 * @param responseType Die Klasse der Response, die Empfangen werden soll
	 */
	public GameLogic(Class<R> responseType) {
		this.responseType = responseType;
		gamestate = new HashMap<>();
		changed = new HashMap<>();
	}
	
	
	/**
	 * Erstellt das Spiel, inseriert alle Keys in den Gamestate (falls dieser verwendet wird)
	 */
	protected abstract void setup();
	/**
	 * Wird aufgerufen, wenn eine Nachricht empfangen wird
	 * 
	 * @param response Die Antwort der AI
	 * @param ai Die AI, welche diese Antwort gesendet hat
	 */
	protected abstract void receive(R response, Ai ai);
	/**
	 * Wird aufgerufen, wenn eine AI aufgegeben hat (oder aufgegeben wurde, z.B. aufgrund illegaler Aktionen oder wenn keine Rechenpunkte mehr übrig sind)
	 * 
	 * @param ai Die AI, welche aufgegeben hat
	 */
	protected abstract void lost(Ai ai);
	/**
	 * Erstellt ein neues AIWrapper Objekt
	 * 
	 * @param ai Die AI, für die das Objekt erstellt werden soll
	 * @return Das AI Objekt
	 */
	protected abstract E createUserObject(Ai ai);
	
	
	/**
	 * Schickt die Änderungen des GameStates an alle AIs
	 * 
	 * @throws IOException
	 */
	protected void sendGameState() throws IOException {
		for(Ai ai : game.getAis()) {
			getUserObject(ai).updateCalculationTimer();
			if(!getUserObject(ai).lost)
				sendToAi(changed, ai);
		}
		changed.clear();
	}
	
	/**
	 * Castet das User Object der AI (Util-Methode)
	 * 
	 * @param ai Die AI, deren Objekt gecastet werden soll
	 * @return Das gecastete User Object
	 */
	@SuppressWarnings("unchecked")
	protected E getUserObject(Ai ai) {
		return (E) ai.getObject();
	}
	
	/**
	 * Setzt einen Schlüssel im Gamestate
	 * 
	 * @param key Der Schlüssel
	 * @param value Der Wert
	 */
	protected void set(String key, String value) {
		gamestate.put(key, value);
		changed.put(key, value);
	}
	
	/**
	 * Holt sich den Wert eines Schlüssels im Gamestate
	 * 
	 * @param key Der Schlüssel
	 * @return Der Wert
	 */
	protected String get(String key) {
		if(!gamestate.containsKey(key)) {
			set(key, "");
			return "";
		}
		return gamestate.get(gamestate);
	}
	
	/**
	 * Wird aufgerufen, wenn eine Nachricht empfangen wurde
	 * 
	 * @param message Die Nachricht
	 * @param ai Die AI, von welcher die Nachricht kommt
	 */
	public void receiveMessage(byte[] message, Ai ai) {
		if(getUserObject(ai).lost) {
			return;
		}
		if(new String(message, UTF_8).equals("SURRENDER")) {
			getUserObject(ai).loose();
			return;
		}
		try {
			receive(Parsers.getWorker().parse(message, responseType), ai);
		} catch (IOException e) {
			getUserObject(ai).loose();
		}
	}
	
	/**
	 * Sendet ein Objekt an das Frontend
	 * 
	 * @param object Das Objekt, das gesendet werden soll
	 */
	public void sendToFronted(Object object) {
		throw new UnsupportedOperationException();
	}
	
	private int update = 1; 
	
	/**
	 * Sendet die Daten zum rendern an das Frontend
	 * 
	 * @param data
	 */
	public void sendRenderData(Object data) {
		RenderData renderData = new RenderData();
		renderData.update = update;
		update++;
		renderData.data = data;
		sendToFronted(renderData);
	}
	
	/**
	 * Sendet ein Objekt an die AI
	 * 
	 * @param object Das Objekt, das gesendet werden soll
	 * @param ai Die AI, der das Objekt gesendet werden soll
	 * @throws IOException
	 */
	public void sendToAi(Object object, Ai ai) throws IOException {
		ai.sendMessage(Parsers.getWorker().parse(object));
	}
	
	/**
	 * Beendet das Spiel (Die scores müssen davor gesetzt werden!)
	 */
	public void endGame() {
		GameFinished message = new GameFinished();
		message.leftoverMillis = new int[game.getAis().size()];
		message.scores = new int[game.getAis().size()];
		for(Ai ai : game.getAis()) {
			message.leftoverMillis[ai.getIndex()] = getUserObject(ai).millisLeft;
			message.scores[ai.getIndex()] = getUserObject(ai).score;
			message.won[ai.getIndex()] = !getUserObject(ai).lost;
		}
		sendToFronted(message);
		game.finishGame();
	}
	
	/**
	 * Startet das Spiel
	 * 
	 * @param game Das Spiel, welches gestartet werden soll
	 */
	public void startGame(Game game) {
		this.game = game;
		for(Ai ai : game.getAis()) {
			ai.setObject(createUserObject(ai));
			getUserObject(ai).setLogic(this);
			getUserObject(ai).setAi(ai);
		}
		setup();
	}
}

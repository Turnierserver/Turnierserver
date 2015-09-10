package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.pixelgaffer.turnierserver.GsonGzipParser;
import org.pixelgaffer.turnierserver.Logger;
import org.pixelgaffer.turnierserver.Parser;
import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;
import org.pixelgaffer.turnierserver.gamelogic.messages.GameFinished;
import org.pixelgaffer.turnierserver.gamelogic.messages.RenderData;

import com.google.common.collect.Ordering;
import com.google.gson.reflect.TypeToken;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @param <E>
 *            Das AiObject
 * @param <R>
 *            Die Antwort der Ai
 */
@NoArgsConstructor
public abstract class GameLogic<E extends AiObject, R> {
	
	public static final Logger logger = new Logger();
	/**
	 * Gibt an, wie viele Runden gespielt werden. -1 bei unbegrenzt vielen Runden.
	 */
	protected int maxTurns = -1;
	
	/**
	 * Gibt an, wie viele Runden schon gespielt wurden
	 */
	protected int playedRounds;
	
	/**
	 * Das Spiel, welches von dieser GameLogic geleitet wird
	 */
	protected Game game;
	
	/**
	 * Der Fortschritt, welcher angezeigt werden soll, -1, wenn keiner angezeigt werden soll
	 */
	protected double progress = -1;
	
	/**
	 * Der Display-String, welcher angezeigt werden soll
	 */
	protected String display;
	
	/**
	 * Gibt an ob das Spiel schon beendet worden ist
	 */
	protected boolean gameEnded = false;
	
	/**
	 * Sortiert Ais absteigend nach Score
	 */
	private AiOrdering ordering = new AiOrdering();
	
	private TypeToken<R> token;
	
	@Getter
	@Setter
	protected boolean started;
	
	public GameLogic(TypeToken<R> token) {
		this.token = token;
	}
	
	/**
     * Erstellt das Spiel, inseriert alle Keys in den Gamestate (falls dieser
     * verwendet wird)
     */
    protected abstract void setup();
	
	/**
     * Wird aufgerufen, wenn eine Nachricht empfangen wird
     * 
     * @param response
     *            Die Antwort der AI
     * @param ai
     *            Die AI, welche diese Antwort gesendet hat
     * @param passedMikros 
     * 			  Die vergangenen Mikros zwischen Antwort eingang und Antwort ausgang bei der KI
     */
    protected abstract void receive(R response, Ai ai, int passedMikros);
	
	/**
     * Wird aufgerufen, wenn eine AI aufgegeben hat (oder aufgegeben wurde, z.B.
     * aufgrund illegaler Aktionen oder wenn keine Rechenpunkte mehr übrig sind)
     * 
     * @param ai
     *            Die AI, welche aufgegeben hat
     */
    public abstract void lost(Ai ai);
	
	/**
     * Erstellt ein neues AIWrapper Objekt
     * 
     * @param ai
     *            Die AI, für die das Objekt erstellt werden soll
     * @return Das AI Objekt
     */
    protected abstract E createUserObject(Ai ai);
	
	/**
     * Wird aufgerufen, wenn endGame() aufgerufen wird. Hier kann Score zeugs
     * implementiert werden.
     */
	protected abstract void gameFinished();
	
	/**
	 * Castet das User Object der AI (Util-Methode)
	 * 
	 * @param ai
	 *            Die AI, deren Objekt gecastet werden soll
	 * @return Das gecastete User Object
	 */
	@SuppressWarnings("unchecked")
	protected E getUserObject(Ai ai) {
		return (E) ai.getObject();
	}
	
	/**
	 * Wird aufgerufen, wenn eine Nachricht empfangen wurde
	 * 
	 * @param message
	 *            Die Nachricht
	 * @param ai
	 *            Die AI, von welcher die Nachricht kommt
	 */
	public void receiveMessage(byte[] message, Ai ai) {
		if(gameEnded) {
			return;
		}
		
		if (getUserObject(ai).lost) {
			return;
		}
		
		String string = null;
		try {
			Parser parser = Parsers.getWorker();
			if(parser instanceof GsonGzipParser) {
				string = new String(((GsonGzipParser)parser).uncompress(message), "UTF-8");
			}
			else {
				string = new String(message, "UTF-8");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			getUserObject(ai).loose("Die Nachricht der KI konnte nicht gelesen werden");
			return;
		}
		
		//Wenn der erste Buchstabe eine Zahl ist, wird die Zahl ausgelesen und geparsed
		int passedMikros = 0;
		if(string.length() > 0 && Character.isDigit(string.charAt(0))) {
			passedMikros = Integer.parseInt(string.substring(0, string.indexOf('{')));
			string = string.substring(string.indexOf('{'));
		}
		
		if (string.equals("SURRENDER")) {
			getUserObject(ai).loose("Die KI hat Aufgegeben");
			return;
		}
		
		if (string.startsWith("CRASH ")) {
			getUserObject(ai).loose("Die KI ist gecrashed: " + string.substring("CRASH ".length()));
			return;
		}
		
		try {
			receive(Parsers.getWorker().parse(string.getBytes(StandardCharsets.UTF_8), token.getType()), ai, passedMikros);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sendet ein Objekt an das Frontend
	 * 
	 * @param object
	 *            Das Objekt, das gesendet werden soll
	 */
	public void sendToFronted(Object object) {
		if(gameEnded) {
			System.err.println("WUUUUUT?? (sendToFrontend aufgerufen)");
			return;
		}
		try {
			game.getFrontend().sendMessage(Parsers.getFrontend().parse(object, false));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Das Renderupdate
	 */
	protected int update = 1;
	
	/**
	 * Sendet die Daten zum rendern an das Frontend
	 * 
	 * @param data
	 */
	public void sendRenderData(Object data) {
		if(gameEnded) {
			System.err.println("WUUUUUT?? (sendRenderData aufgerufen)");
			return;
		}
		RenderData renderData = new RenderData();
		renderData.update = update;
		update++;
		renderData.data = data;
		renderData.requestid = game.getFrontend().getRequestId();
		renderData.progress = progress;
		if (display != null) {
			renderData.display = display;
		}
		renderData.calculationPoints = new HashMap<>();
		renderData.points = new HashMap<>();
		for(Ai ai : game.getAis()) {
			renderData.calculationPoints.put(ai.getId(), getUserObject(ai).mikrosLeft);
			renderData.points.put(ai.getId(), getUserObject(ai).score);
		}
		sendToFronted(renderData);
	}
	
	/**
	 * Sendet ein Objekt an die AI
	 * 
	 * @param object
	 *            Das Objekt, das gesendet werden soll
	 * @param ai
	 *            Die AI, der das Objekt gesendet werden soll
	 * @throws IOException
	 */
	public void sendToAi(Object object, Ai ai) throws IOException {
		if(gameEnded) {
			System.err.println("WUUUUUT?? (sendToAi aufgerufen)");
			return;
		}
		ai.sendMessage(Parsers.getWorker().parse(object, false));
	}
	
	/**
	 * Beendet das Spiel (Die scores müssen davor gesetzt werden!)
	 */
	public void endGame(String reason) {
		if(gameEnded) {
			System.err.println("WUUUUUT?? (endGame aufgerufen)");
			return;
		}
		gameFinished();
		
		GameFinished message = new GameFinished();
		message.leftoverMillis = new HashMap<>();
		message.scores = new HashMap<>();
		message.position = new HashMap<>();
		message.requestid = game.getFrontend().getRequestId();
		message.reason = reason;
		
		int pos = 1;
		
		for (Ai ai : ordering.sortedCopy(game.getAis())) {
			message.leftoverMillis.put(ai.getId(), getUserObject(ai).mikrosLeft);
			message.scores.put(ai.getId(), getUserObject(ai).score);
			message.position.put(ai.getId(), pos);
			pos++;
		}
		
		sendToFronted(message);
		gameEnded = true;
		try {
			game.finishGame();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Startet das Spiel
	 * 
	 * @param game
	 *            Das Spiel, welches gestartet werden soll
	 */
	public void startGame(Game game) {
		this.game = game;
		for (Ai ai : game.getAis()) {
			ai.setObject(createUserObject(ai));
			getUserObject(ai).setLogic(this);
			getUserObject(ai).setAi(ai);
		}
		setup();
	}
	
	/**
	 * Erhöht playedRounds um 1
	 */
	public void round() {
		playedRounds++;
	}
	
	/**
	 * Gibt zurück, ob die Anzahl an gespielten Runden der Anzahl an zu spielenden Runden ist
	 * 
	 * @return True, wenn alle zu spielenden Runden gespielt wurden
	 */
	public boolean allRoundsPlayed() {
		return playedRounds == maxTurns && maxTurns > 0;
	}
	
	/**
	 * @return Die Anzahl an Spielern, für welche dieses Spiel ausgelegt ist
	 */
	public int playerAmt() {
		return 2;
	}
	
	/**
	 * Teilt der Spiellogik mit, wenn sich eine Ai disconnected
	 * 
	 * @param ai Die Ai, die sich disconnected hat
	 */
	public void aiCrashed(Ai ai) {
		if(!getUserObject(ai).lost) {
			getUserObject(ai).loose("Die Ki hat sich disconnected. Dies kann daran liegen, dass ein Crash nicht abgefangen wurde, oder dass die Ki ihre verfügbare Zeit verbraucht hat und von der Sandbox terminiert wurde.");
		}
	}
	
	/**
	 * Sortiert Ais aufsteigend nach Score
	 * 
	 */
	public static class AiOrdering extends Ordering<Ai> {
		
		@Override
		public int compare(Ai ai1, Ai ai2) {
			AiObject o1 = ai1.getObject();
			AiObject o2 = ai2.getObject();
			return Integer.compare(o2.score, o1.score);
		}
	}
	
	public Game getGame() {
		return game;
	}
	
}

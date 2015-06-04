package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.AiObject;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Game;
import org.pixelgaffer.turnierserver.gamelogic.messages.GameFinished;
import org.pixelgaffer.turnierserver.gamelogic.messages.RenderData;

import com.google.common.collect.Ordering;
import com.google.gson.reflect.TypeToken;

/**
 * @param <E>
 *            Das AiObject
 * @param <R>
 *            Die Antwort der Ai
 */
@NoArgsConstructor
public abstract class GameLogic<E extends AiObject, R> {

	public static final Logger logger = Logger.getLogger("GameLogic");
	/**
	 * Gibt an, wie viele Runden gespielt werden. -1 bei unbegrenzt vielen
	 * Runden.
	 */
	@Getter
	@Setter
	private int maxTurns = -1;

	/**
	 * Gibt an, wie viele Runden schon gespielt wurden
	 */
	@Getter
	private int playedRounds;

	/**
	 * Das Spiel, welches von dieser GameLogic geleitet wird
	 */
	@Getter
	protected Game game;

	/**
	 * Sortiert Ais absteigend nach Score
	 */
	private AiOrdering ordering = new AiOrdering();

	private TypeToken<R> token;

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
	 */
	protected abstract void receive(R response, Ai ai);

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
		logger.info("Eine Nachricht wurde von einer AI(" + ai.getIndex() + ") empfangen: " + new String(message, StandardCharsets.UTF_8));
		if (getUserObject(ai).lost) {
			logger.info("Die ai hat schon verloren");
			return;
		}
		if (new String(message, StandardCharsets.UTF_8).equals("SURRENDER")) {
			logger.info("Di ai hat aufgegeben");
			getUserObject(ai).loose();
			return;
		}
		try {
			receive(Parsers.getWorker().parse(message, token.getType()), ai);
			logger.finest("Nachricht erfolgreich empfangen");
		} catch (IOException e) {
			logger.severe("Eine Nachricht konnte nicht gelesen werden: " + e.getLocalizedMessage());
			e.printStackTrace();
			getUserObject(ai).loose();
		}
	}

	/**
	 * Sendet ein Objekt an das Frontend
	 * 
	 * @param object
	 *            Das Objekt, das gesendet werden soll
	 */
	public void sendToFronted(Object object) {
		try {
			game.getFrontend().sendMessage(Parsers.getFrontend().parse(object));
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
		RenderData renderData = new RenderData();
		renderData.update = update;
		update++;
		renderData.data = data;
		renderData.requestid = game.getFrontend().getRequestId();
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
		ai.sendMessage(Parsers.getWorker().parse(object));
	}

	/**
	 * Beendet das Spiel (Die scores müssen davor gesetzt werden!)
	 */
	public void endGame() {
		GameFinished message = new GameFinished();
		message.leftoverMillis = new HashMap<>();
		message.scores = new HashMap<>();
		message.position = new HashMap<>();
		message.requestid = game.getFrontend().getRequestId();
		
		int pos = 1;

		for (Ai ai : ordering.sortedCopy(game.getAis())) {
			message.leftoverMillis.put(ai.getId(), getUserObject(ai).millisLeft);
			message.scores.put(ai.getId(), getUserObject(ai).score);
			message.position.put(ai.getId(), pos);
			pos++;
		}
		
		sendToFronted(message);
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
	 * Sortiert Ais aufsteigend nach Score
	 * 
	 */
	public static class AiOrdering extends Ordering<Ai> {
		@Override
		public int compare(Ai ai1, Ai ai2) {
			AiObject o1 = ai1.getObject();
			AiObject o2 = ai2.getObject();
			return Integer.compare(o1.score, o2.score);
		}
	}

}

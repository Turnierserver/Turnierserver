package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.pixelgaffer.turnierserver.backend.AiWrapper;

public abstract class TurnBasedGameLogic<E, R> extends GameLogic<E, R> {
	
	private Set<AiWrapper> received;
	
	public TurnBasedGameLogic(Class<R> responseType) {
		super(responseType);
		received = new HashSet<>();
	}

	protected abstract void update();
	
	protected abstract void processResponse(R message, AiWrapper ai);
	
	@Override
	protected void receive(R response, AiWrapper ai) {
		if(received.contains(ai)) {
			return;
		}
		received.add(ai);
		processResponse(response, ai);
		if(received.size() == game.getAiCount()) {
			update();
			try {
				sendGameState();
			} catch (IOException e) {
				e.printStackTrace();
			}
			received.clear();
		}
	}
	
	protected void start() throws UnsupportedEncodingException, IOException {
		sendGameState();
	}
	
}

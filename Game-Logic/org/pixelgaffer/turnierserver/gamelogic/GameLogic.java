package org.pixelgaffer.turnierserver.gamelogic;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.MessagePack;
import org.pixelgaffer.turnierserver.backend.AiWrapper;
import org.pixelgaffer.turnierserver.backend.Game;

public abstract class GameLogic<E, R> {
	
	protected Game game;
	protected Class<R> responseType;
	
	private Map<String, String> gamestate;
	private Map<String, String> changed;
	private MessagePack msgpack;
	
	public GameLogic(Class<R> responseType) {
		this.responseType = responseType;
		gamestate = new HashMap<>();
		changed = new HashMap<>();
	}
	
	
	
	protected abstract void setup();
	protected abstract void receive(R response, AiWrapper ai);
	
	
	
	protected void sendGameState() throws UnsupportedEncodingException, IOException {
		for(AiWrapper ai : game.getAis()) {
			ai.sendMessage(new String(msgpack.write(changed), "UTF-8"));
		}
		changed.clear();
	}
	
	@SuppressWarnings("unchecked")
	protected E getUserObject(AiWrapper ai) {
		return (E) ai.getUserObject();
	}
	
	protected void set(String key, String value) {
		gamestate.put(key, value);
		changed.put(key, value);
	}
	
	protected String get(String key) {
		if(!gamestate.containsKey(key)) {
			set(key, "");
			return "";
		}
		return gamestate.get(gamestate);
	}
	
	
	
	
	
	public void receiveMessage(String message, AiWrapper ai) {
		try {
			receive(msgpack.read(message.getBytes("UTF-8"), responseType), ai);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startGame(Game game) {
		this.game = game;
	}
	
}

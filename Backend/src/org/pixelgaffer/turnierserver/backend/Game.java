package org.pixelgaffer.turnierserver.backend;

import java.util.Set;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.pixelgaffer.turnierserver.gamelogic.GameLogic;

@AllArgsConstructor
public class Game
{
	@Getter
	private GameLogic logic;
	
	@Getter
	private Set<AiWrapper> ais = new TreeSet<>();
	
	public int getAiCount ()
	{
		return ais.size();
	}
}

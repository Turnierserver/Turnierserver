package org.pixelgaffer.turnierserver.minesweeper.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.pixelgaffer.turnierserver.backend.AiWrapper;
import org.pixelgaffer.turnierserver.gamelogic.GameLogic;
import org.pixelgaffer.turnierserver.minesweeper.Cell;
import org.pixelgaffer.turnierserver.minesweeper.Grid;
import org.pixelgaffer.turnierserver.minesweeper.MinesweeperResponse;

public class MinesweeperLogic extends GameLogic<MinesweeperObject, MinesweeperResponse> {
			
	private Random random;
	
	private Grid[] generated;
	private int[] steps;
	private ArrayList<MinesweeperRenderData>[] renderData;
		
	public MinesweeperLogic() {
		super(MinesweeperResponse.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setup() {
		generated = new Grid[game.getAiCount()];
		steps = new int[game.getAiCount()];
		renderData = new ArrayList[game.getAiCount()];
		for(AiWrapper ai : game.getAis()) {
			sendFieldRequest(ai);
		}
	}

	@Override
	protected void receive(MinesweeperResponse response, AiWrapper ai) {
		MinesweeperObject obj = getUserObject(ai);
		
		if(generated[ai.getId()] == null)  {
			obj.stopCalculationTimer();
			if(response.newField == null) {
				obj.loose();
				return;
			}
			
			Grid grid = new Grid(response.newField);
			generated[ai.getId()] = grid;
			
			MinesweeperRenderData data = new MinesweeperRenderData();
			data.calculationTime = obj.millisLeft;
			data.field = response.newField;
			data.output = response.output;
			renderData[ai.getId()].add(data);
			
			if(grid.hasEmpty() || grid.getBombs() != Cell.BOMB_COUNT) {
				obj.loose();
				return;
			}
			
			if(checkAllGenerated()) {
				for(AiWrapper wrapper : game.getAis()) {
					sendGridToAi(wrapper, generated[getGrid(ai)]);
				}
			}
			return;
		}
		
		Grid grid = generated[getGrid(ai)];
		
		if(grid.won() || obj.lost) {
			obj.loose();
			return;
		}
		
		obj.stopCalculationTimer();
		
		boolean didSomething = false;
		
		steps[ai.getId()]++;
		
		Map<String, String> request = new HashMap<>();
		request.put("creating", "false");
		
		if(Cell.isInField(response.xFlag, response.yFlag)) {
			if(!grid.get(response.xFlag, response.yFlag).isFlagged()) {
				grid.get(response.xFlag, response.yFlag).setFlagged(true);
				request.put(response.xFlag + ":" + response.yFlag, grid.get(response.xFlag, response.yFlag).toString());
				didSomething = true;
			}
		}
		if(Cell.isInField(response.xStep, response.yStep)) {
			Map<String, String> map = grid.uncover(response.xStep, response.yStep);
			if(map == null) {
				obj.loose();
				return;
			}
			request.putAll(map);
			didSomething = true;
		}
		
		if(grid.won()) {
			obj.score = steps[ai.getId()];
			if(checkAllSolved()) {
				finishGame();
			}
			return;
		}
		
		try {
			sendToAi(request, ai);
			obj.startCalculationTimer(10);
		} catch (IOException e) {
			obj.loose();
		}
		
		if(didSomething) {
			MinesweeperRenderData data = new MinesweeperRenderData();
			data.calculationTime = obj.millisLeft;
			data.field = grid.toMap();
			data.output = response.output;
			renderData[ai.getId()].add(data);
		}
		
	}

	@Override
	protected void lost(AiWrapper ai) {
		getUserObject(ai).score = (int) (-Math.pow(Cell.FIELD_SIZE, 2) + steps[ai.getId()]);
	}

	@Override
	protected MinesweeperObject createUserObject(AiWrapper ai) {
		MinesweeperObject o = new MinesweeperObject();
		o.millisLeft = 10000;
		return o;
	}
	
	private boolean checkAllGenerated() {
		for(int i = 0; i < generated.length; i++) {
			if(generated[i] == null) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkAllSolved() {
		for(AiWrapper ai : game.getAis()) {
			if(!generated[getGrid(ai)].won() && !getUserObject(ai).lost) {
				return false;
			}
		}
		return true;
	}
	
	private int getGrid(AiWrapper ai) {
		return (ai.getId() - 1) % game.getAiCount();
	}
	
	private void sendGridToAi(AiWrapper ai, Grid grid) {
		Map<String, String> request = new HashMap<>();
		request.put("creating", "false");
		request.putAll(grid.toMap());
		try {
			sendToAi(request, ai);
			getUserObject(ai).startCalculationTimer(10);
		} catch (IOException e) {
			getUserObject(ai).loose();
		}
	}
	
	private void sendFieldRequest(AiWrapper ai) {
		Map<String, String> request = new HashMap<>();
		request.put("creating", "true");
		try {
			sendToAi(request, ai);
			getUserObject(ai).startCalculationTimer(10);
		} catch (IOException e) {
			getUserObject(ai).loose();
		}
	}
	
	private void finishGame() {
		for(List<MinesweeperRenderData> dataList : renderData) {
			for(MinesweeperRenderData data : dataList) {
				sendToFronted(data);
			}
		}
		endGame();
	}

}

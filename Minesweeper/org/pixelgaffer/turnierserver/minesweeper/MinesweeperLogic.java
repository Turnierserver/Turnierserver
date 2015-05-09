package org.pixelgaffer.turnierserver.minesweeper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.pixelgaffer.turnierserver.backend.AiWrapper;
import org.pixelgaffer.turnierserver.gamelogic.GameLogic;
import org.pixelgaffer.turnierserver.minesweeper.Cell.Type;

public class MinesweeperLogic extends GameLogic<MinesweeperObject, MinesweeperResponse> {
		
	private Cell[][] field;
	
	private Random random;
	
	private int waitingForField = -1;
	private int waitingForResponse = -1;
		
	public MinesweeperLogic() {
		super(MinesweeperResponse.class);
	}

	@Override
	protected void setup() {
		sendFieldRequest(game.getAis().toArray(new AiWrapper[game.getAiCount()])[random.nextInt(game.getAiCount())]);
	}

	@Override
	protected void receive(MinesweeperResponse response, AiWrapper ai) {
		if(waitingForField != -1) {
			if(ai.getId() != waitingForField) {
				getUserObject(ai).loose();
				return;
			}
			if(getUserObject(ai).stopCalculationTimer()) {
				return;
			}
			
			waitingForField = -1;
			
			if(response.newField == null || response.newField.isEmpty()) {
				getUserObject(ai).loose();
				return;
			}
			field = Cell.toCellArray(response.newField);
			
			int bombs = 0;
			for(int i = 0; i < Cell.FIELD_SIZE; i++) {
				for(int j = 0; j < Cell.FIELD_SIZE; j++) {
					Cell cell = field[i][j];
					if(cell.getType() == Type.BOMB) {
						bombs++;
					}
					if(cell.getType() == Type.EMPTY) {
						cell.countSurroundingBombs(field, i, j);
					}
					if(cell.getType() == Type.COVERED) {
						getUserObject(ai).loose();
						return;
					}
				}
			}
			if(bombs != Cell.BOMB_COUNT) {
				getUserObject(ai).loose();
				return;
			}
			
			
			
			AiWrapper sendTo = null;
			for(AiWrapper wrapper : game.getAis()) {
				if(wrapper.getId() != ai.getId()) {
					sendTo = wrapper;
				}
			}
			try {
				sendToAi(Cell.toMap(field), sendTo);
			} catch (IOException e) {
				getUserObject(sendTo).loose();
				return;
			}
			waitingForResponse = sendTo.getId();
			getUserObject(sendTo).startCalculationTimer(10);
			return;
		}
		
		if(waitingForResponse != ai.getId()) {
			getUserObject(ai).loose();
			return;
		}
		
		if(getUserObject(ai).stopCalculationTimer()) {
			return;
		}
		
		boolean didSomething = false;
		
		if(response.xFlag != -1 && response.yFlag != -1) {
			if(!Cell.isInField(response.xFlag, response.yFlag)) {
				getUserObject(ai).loose();
				return;
			}
			
			didSomething = true;
			Cell flag = field[response.xFlag][response.yFlag];
			flag.setFlagged(!flag.isFlagged());
		}
		
		if(response.xStep != -1 && response.yStep != -1) {
			if(!Cell.isInField(response.xStep, response.yStep)) {
				getUserObject(ai).loose();
				return;
			}
			
			didSomething = true;
			Map<String, String> changes = field[response.xStep][response.yStep].uncover(field, response.xStep, response.yStep);
			if(changes == null) {
				MinesweeperRenderData data = new MinesweeperRenderData();
				data.output = response.output;
				data.field = Cell.toMap(field);
				data.solving = ai.getId();
				data.calculationTime = getUserObject(ai).millisLeft;
				sendRenderData(data);
				
				getUserObject(ai).loose();
				return;
			}
			boolean won = false;
			outer: for(Cell[] line : field) {
				for(Cell cell :line) {
					if(!cell.isUncovered() && cell.getType() != Type.BOMB) {
						won = false;
						break outer;
					}
				}
			}
			if(won) {
				sendFieldRequest(game.getAis().toArray(new AiWrapper[game.getAiCount()])[ai.getId() == 0 ? 1 : 0]);
				waitingForResponse = -1;
				return;
			}
			
			try {
				sendToAi(changes, ai);
			} catch (IOException e) {
				getUserObject(ai).loose();
			}
			return;
		}
		
		if(!didSomething) {
			return;
		}
		
		MinesweeperRenderData data = new MinesweeperRenderData();
		data.output = response.output;
		data.field = Cell.toMap(field);
		data.solving = ai.getId();
		data.calculationTime = getUserObject(ai).millisLeft;
		sendRenderData(data);
		getUserObject(ai).startCalculationTimer(10);
	}

	@Override
	protected void lost(AiWrapper ai) {
		for(AiWrapper wrapper : game.getAis()) {
			getUserObject(wrapper).score = getUserObject(wrapper).lost ? -1 : 1;
		}
		endGame();
	}

	@Override
	protected MinesweeperObject createUserObject(AiWrapper ai) {
		MinesweeperObject o = new MinesweeperObject();
		o.millisLeft = 10000;
		return o;
	}
	
	private void sendFieldRequest(AiWrapper ai) {
		Map<String, String> request = new HashMap<>();
		request.put("creating", "true");
		try {
			sendToAi(request, ai);
			getUserObject(ai).startCalculationTimer(10);
			waitingForField = ai.getId();
		} catch (IOException e) {
			getUserObject(ai).loose();
		}
	}

}

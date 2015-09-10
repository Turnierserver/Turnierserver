package org.pixelgaffer.turnierserver.codr.view;


import java.util.ArrayList;
import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;

import org.pixelgaffer.turnierserver.codr.AiBase;
import org.pixelgaffer.turnierserver.codr.AiOnline;
import org.pixelgaffer.turnierserver.codr.AiSimple;
import org.pixelgaffer.turnierserver.codr.GameBase;
import org.pixelgaffer.turnierserver.codr.GameBase.GameMode;
import org.pixelgaffer.turnierserver.codr.GameOnline;
import org.pixelgaffer.turnierserver.codr.GameSaved;
import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.codr.ParticipantResult;
import org.pixelgaffer.turnierserver.codr.Version;
import org.pixelgaffer.turnierserver.codr.utilities.Dialog;



public class ControllerGameManagement {
	
	
	@FXML public TabPane tabPaneOnOffline;
	@FXML public ListView<AiSimple> lvPlayerOffline1;
	@FXML public ListView<AiSimple> lvPlayerOffline2;
	@FXML public ListView<AiOnline> lvPlayerOnline1;
	@FXML public ListView<AiOnline> lvPlayerOnline2;
	@FXML public TableView<GameOnline> lvGamesOnline;
	@FXML public TableView<GameSaved> lvGamesOffline;
	@FXML public TitledPane tpNewGameOnline;
	@FXML public Tab tabOnline;
	@FXML public Tab tabOffline;
	
	
	public GameSaved runningGame;
	
	MainApp mainApp;
	
	public GameBase game = null;
	
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app) {
		mainApp = app;
		MainApp.cGame = this;
				
		MainApp.gameManager.loadGames();
//		showGame();
		
		lvPlayerOffline1.setItems(MainApp.aiManager.ais);
		lvPlayerOffline2.setItems(MainApp.aiManager.ais);

		lvPlayerOnline1.setItems(MainApp.ownOnlineAis);
		lvPlayerOnline2.setItems(MainApp.onlineAis);
		
		lvGamesOffline.setItems(MainApp.gameManager.games);
		lvGamesOnline.setItems(MainApp.onlineGames);

		MainApp.aiManager.ais.addListener(new ListChangeListener<AiSimple>() {
			@Override public void onChanged(ListChangeListener.Change<? extends AiSimple> change) {
				initialSelectOffline();
			}
		});
		MainApp.ownOnlineAis.addListener(new ListChangeListener<AiOnline>() {
			@Override public void onChanged(ListChangeListener.Change<? extends AiOnline> change) {
				initialSelectOnline();
			}
		});
		MainApp.onlineAis.addListener(new ListChangeListener<AiOnline>() {
			@Override public void onChanged(ListChangeListener.Change<? extends AiOnline> change) {
				initialSelectOnline();
			}
		});

	}
	
	
	public void initialSelectOffline(){
		if (lvPlayerOffline1.getItems().size() > 0){
			lvPlayerOffline1.getSelectionModel().select(0);
		}
		if (lvPlayerOffline2.getItems().size() > 1){
			lvPlayerOffline2.getSelectionModel().select(1);
		}
	}
	public void initialSelectOnline(){
		if (lvPlayerOnline1.getItems().size() > 0){
			lvPlayerOnline1.getSelectionModel().select(0);
		}
		if (lvPlayerOnline2.getItems().size() > 1 && lvPlayerOnline1.getItems().size() > 0){
			if (lvPlayerOnline1.getItems().get(0).equals(lvPlayerOnline2.getItems().get(0)))
				lvPlayerOnline2.getSelectionModel().select(1);
			else
				lvPlayerOnline2.getSelectionModel().select(0);
		}
	}
	
//	public void showGame(GameBase ggame) {
//		game = ggame;
//		showGame();
//	}
//	
//	
//	public void showGame() {
//	}
	

	@FXML void clickStartGameOnline() {
		
	}
	
	
	@FXML void clickStartGameOffline() {
		if (lvPlayerOffline1.getSelectionModel().getSelectedItem() == null || lvPlayerOffline2.getSelectionModel().getSelectedItem() == null) {
			Dialog.error("Bitte erst die KIs auswählen");
			return;
		}
		if (lvPlayerOffline1.getSelectionModel().getSelectedItem() == lvPlayerOffline2.getSelectionModel().getSelectedItem()){
			Dialog.error("Bitte nicht die gleiche KI auswählen");
			return;
		}
		
		Task<Boolean> play = new Task<Boolean>() {
			public Boolean call() {
				System.out.println("starte game :)");
				runningGame = new GameSaved(MainApp.actualGameType.get());
				List<Version> players = new ArrayList<>();
				players.add(lvPlayerOffline1.getSelectionModel().getSelectedItem().lastVersion());
				players.add(lvPlayerOffline2.getSelectionModel().getSelectedItem().lastVersion());
				runningGame.play(players);
				return true;
			}
		};
		
		Thread thread = new Thread(play, "play");
		thread.setDaemon(true);
		thread.start();
	}
	
}

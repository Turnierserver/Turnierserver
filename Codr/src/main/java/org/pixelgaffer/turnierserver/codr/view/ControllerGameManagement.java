package org.pixelgaffer.turnierserver.codr.view;


import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

import org.pixelgaffer.turnierserver.codr.AiBase;
import org.pixelgaffer.turnierserver.codr.GameBase;
import org.pixelgaffer.turnierserver.codr.GameBase.GameMode;
import org.pixelgaffer.turnierserver.codr.GameSaved;
import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.codr.ParticipantResult;
import org.pixelgaffer.turnierserver.codr.Version;
import org.pixelgaffer.turnierserver.codr.utilities.Dialog;



public class ControllerGameManagement {
	
	
	@FXML public Label lbMode;
	@FXML public Label lbDate;
	@FXML public Label lbDuration;
	@FXML public Label lbLogic;
	@FXML public Label lbChallenger;
	@FXML public Label lbJudged;
	@FXML public ToggleButton btOffline;
	@FXML public ToggleButton btOnline;
	@FXML public ChoiceBox<String> cbLogic;
	@FXML public ListView<AiBase> lvPlayer1;
	@FXML public ListView<AiBase> lvPlayer2;
	@FXML public ProgressIndicator progress;
	@FXML public Button btLoadOnline;
	@FXML public TextArea tbOutput1;
	@FXML public TextArea tbOutput2;
	@FXML public TableView<ParticipantResult> tableResult;
	
	@FXML public GameSaved runningGame;
	
	MainApp mainApp;
	
	GameBase game = null;
	
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app) {
		mainApp = app;
		MainApp.cGame = this;
		
		TableColumn<ParticipantResult, String> col0 = new TableColumn<>("Spieler");
		TableColumn<ParticipantResult, String> col1 = new TableColumn<>("KI");
		TableColumn<ParticipantResult, String> col2 = new TableColumn<>("gespielte Zeit");
		TableColumn<ParticipantResult, String> col3 = new TableColumn<>("Züge");
		TableColumn<ParticipantResult, String> col4 = new TableColumn<>("Punkte");
		TableColumn<ParticipantResult, String> col5 = new TableColumn<>("Gewonnen?");
		
		col0.setCellValueFactory(new PropertyValueFactory<ParticipantResult, String>("playerName"));
		col1.setCellValueFactory(new PropertyValueFactory<ParticipantResult, String>("kiName"));
		col2.setCellValueFactory(new PropertyValueFactory<ParticipantResult, String>("duration"));
		col3.setCellValueFactory(new PropertyValueFactory<ParticipantResult, String>("moveCount"));
		col4.setCellValueFactory(new PropertyValueFactory<ParticipantResult, String>("points"));
		col5.setCellValueFactory(new PropertyValueFactory<ParticipantResult, String>("won"));
		
		tableResult.getColumns().add(col0);
		tableResult.getColumns().add(col1);
		tableResult.getColumns().add(col2);
		tableResult.getColumns().add(col3);
		tableResult.getColumns().add(col4);
		tableResult.getColumns().add(col5);
		
		final ToggleGroup group = new ToggleGroup();
		btOffline.setToggleGroup(group);
		btOffline.setSelected(true);
		btOnline.setToggleGroup(group);
		
		MainApp.gameManager.loadGames();
		showGame();
		
		lvPlayer1.getItems().clear();
		lvPlayer2.getItems().clear();
		lvPlayer1.getItems().addAll(MainApp.aiManager.ais);
		lvPlayer2.getItems().addAll(MainApp.aiManager.ais);
	}
	
	
	public void showGame(GameBase ggame) {
		game = ggame;
		showGame();
	}
	
	
	public void showGame() {
		if (game != null) {
			if (game.mode == GameMode.onlineLoaded)
				lbMode.setText("Online");
			else
				lbMode.setText("Offline");
			lbDate.setText(game.date);
			lbDuration.setText(game.duration);
			lbLogic.setText(game.logic);
			tableResult.setItems(game.participants);
		} else {
			lbMode.setText("Offline");
			lbDate.setText("Jetzt");
			lbDuration.setText("555ms");
			lbLogic.setText("Test-Logik");
			tableResult.setItems(null);
		}
	}
	
	
	
	@FXML void clickOnline() {
		mainApp.loadOnlineAis();
		lvPlayer1.getItems().clear();
		lvPlayer1.getItems().addAll(MainApp.ownOnlineAis);
		lvPlayer2.getItems().clear();
		lvPlayer2.getItems().addAll(MainApp.onlineAis);
		lvPlayer1.getSelectionModel().selectFirst();
		lvPlayer2.getSelectionModel().select(1);
	}
	
	
	@FXML void clickOffline() {
		lvPlayer1.getItems().clear();
		lvPlayer2.getItems().clear();
		lvPlayer1.getItems().addAll(MainApp.aiManager.ais);
		lvPlayer2.getItems().addAll(MainApp.aiManager.ais);
		lvPlayer1.getSelectionModel().selectFirst();
		lvPlayer2.getSelectionModel().select(1);
	}
	
	
	@FXML void clickStartGame() {
		if (btOffline.isSelected()) {
			if (lvPlayer1.getSelectionModel().getSelectedItem() != null && lvPlayer2.getSelectionModel().getSelectedItem() != null) {
				Task<Boolean> play = new Task<Boolean>() {
					public Boolean call() {
						runningGame = new GameSaved(MainApp.actualGameType.get(), GameMode.playing);
						List<Version> players = new ArrayList<>();
						players.add(lvPlayer1.getSelectionModel().getSelectedItem().lastVersion());
						players.add(lvPlayer2.getSelectionModel().getSelectedItem().lastVersion());
						runningGame.play(players);
						return true;
					}
				};
				
				Thread thread = new Thread(play, "play");
				thread.setDaemon(true);
				thread.start();
			} else {
				Dialog.error("Bitte erst die KIs auswählen");
			}
		} else {
			Dialog.error("Onlinespiele werden noch nicht unterstützt");
		}
	}
	
	
	@FXML void clickLoadSaved() {
		tbOutput1.setText("Info5 geklickt");
	}
	
	
	@FXML void clickLoadOnline() {
		tbOutput1.setText("Info6 geklickt");
	}
}

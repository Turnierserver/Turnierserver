package org.pixelgaffer.turnierserver.codr.view;


import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import org.pixelgaffer.turnierserver.codr.AiOnline;
import org.pixelgaffer.turnierserver.codr.AiSimple;
import org.pixelgaffer.turnierserver.codr.GameBase;
import org.pixelgaffer.turnierserver.codr.GameOnline;
import org.pixelgaffer.turnierserver.codr.GameSaved;
import org.pixelgaffer.turnierserver.codr.MainApp;
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

		
		lvGamesOffline.setItems(MainApp.gameManager.games);
		lvGamesOnline.setItems(MainApp.onlineGames);
		
		
		
		TableColumn<GameSaved, Image> col0 = new TableColumn<GameSaved, Image>("Spieler 1");
		TableColumn<GameSaved, String> col1 = new TableColumn<GameSaved, String>("Spieler 2");
		TableColumn<GameSaved, String> col2 = new TableColumn<GameSaved, String>("Wann");
		
		col0.setCellValueFactory(new Callback<CellDataFeatures<GameSaved, Image>, ObservableValue<Image>>() {
			@Override
			public ObservableValue<Image> call(CellDataFeatures<GameSaved, Image> arg0) {
				return arg0.getValue().getPicture();
			}
		});
		col0.setCellFactory(new Callback<TableColumn<GameSaved, Image>, TableCell<GameSaved, Image>>() {
			@Override
			public TableCell<GameSaved, Image> call(TableColumn<GameSaved, Image> param) {
				final ImageView imageview = new ImageView();
				imageview.setFitHeight(50);
				imageview.setFitWidth(50);
				
				TableCell<AiOnline, Image> cell = new TableCell<AiOnline, Image>() {
					public void updateItem(Image item, boolean empty) {
						if (item != null)
							imageview.imageProperty().set(item);
					}
				};
				cell.setGraphic(imageview);
				return cell;
			}
			
		});
		col1.setCellValueFactory(new Callback<CellDataFeatures<GameSaved, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<GameSaved, String> p) {
				return new SimpleStringProperty(p.getValue().title);
			}
		});
		col2.setCellValueFactory(new Callback<CellDataFeatures<GameSaved, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<GameSaved, String> p) {
				return new SimpleStringProperty(p.getValue().userName);
			}
		});
		
		col0.setStyle("-fx-alignment: CENTER-LEFT;");
		col1.setStyle("-fx-alignment: CENTER-LEFT;");
		col2.setStyle("-fx-alignment: CENTER-LEFT;");
		
		lvGamesOffline.getColumns().add(col0);
		lvGamesOffline.getColumns().add(col1);
		lvGamesOffline.getColumns().add(col2);
		
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

package org.pixelgaffer.turnierserver.codr.view;


import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.cookie.Cookie;
import org.pixelgaffer.turnierserver.codr.AiOnline;
import org.pixelgaffer.turnierserver.codr.AiSimple;
import org.pixelgaffer.turnierserver.codr.GameBase;
import org.pixelgaffer.turnierserver.codr.GameOnline;
import org.pixelgaffer.turnierserver.codr.GameSaved;
import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.codr.Version;
import org.pixelgaffer.turnierserver.codr.utilities.Dialog;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;



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
	@FXML public WebView webView;
	@FXML public ProgressIndicator prStartGameOnline;
	@FXML public ProgressIndicator prStartGameOffline;
	
	
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
		
		
		
		TableColumn<GameSaved, String> colOff0 = new TableColumn<GameSaved, String>("Spieler 1");
		TableColumn<GameSaved, String> colOff1 = new TableColumn<GameSaved, String>("Wann");
		TableColumn<GameSaved, String> colOff2 = new TableColumn<GameSaved, String>("Spieler 2");

		colOff0.setCellValueFactory(new Callback<CellDataFeatures<GameSaved, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<GameSaved, String> p) {
				if (p.getValue().participants.size() > 0)
					return new SimpleStringProperty(p.getValue().participants.get(0).name);
				else
					return new SimpleStringProperty("ungültig");
			}
		});
		colOff1.setCellValueFactory(new Callback<CellDataFeatures<GameSaved, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<GameSaved, String> p) {
				return new SimpleStringProperty(p.getValue().date);
			}
		});
		colOff2.setCellValueFactory(new Callback<CellDataFeatures<GameSaved, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<GameSaved, String> p) {
				if (p.getValue().participants.size() > 1)
					return new SimpleStringProperty(p.getValue().participants.get(1).name);
				else
					return new SimpleStringProperty("ungültig");
			}
		});
		
		colOff0.setStyle("-fx-alignment: CENTER-LEFT;");
		colOff1.setStyle("-fx-alignment: CENTER-LEFT;");
		colOff2.setStyle("-fx-alignment: CENTER-LEFT;");
		
		lvGamesOffline.getColumns().add(colOff0);
		lvGamesOffline.getColumns().add(colOff1);
		lvGamesOffline.getColumns().add(colOff2);
		

		
		TableColumn<GameOnline, String> colOn0 = new TableColumn<GameOnline, String>("Spieler 1");
		TableColumn<GameOnline, String> colOn1 = new TableColumn<GameOnline, String>("Wann");
		TableColumn<GameOnline, String> colOn2 = new TableColumn<GameOnline, String>("Spieler 2");

		colOn0.setCellValueFactory(new Callback<CellDataFeatures<GameOnline, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<GameOnline, String> p) {
				if (p.getValue().participants.size() > 0)
					return new SimpleStringProperty(p.getValue().participants.get(0).name);
				else
					return new SimpleStringProperty("ungültig");
			}
		});
		colOn1.setCellValueFactory(new Callback<CellDataFeatures<GameOnline, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<GameOnline, String> p) {
				return new SimpleStringProperty(p.getValue().date);
			}
		});
		colOn2.setCellValueFactory(new Callback<CellDataFeatures<GameOnline, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<GameOnline, String> p) {
				if (p.getValue().participants.size() > 1)
					return new SimpleStringProperty(p.getValue().participants.get(1).name);
				else
					return new SimpleStringProperty("ungültig");
			}
		});
		
		colOn0.setStyle("-fx-alignment: CENTER-LEFT;");
		colOn1.setStyle("-fx-alignment: CENTER-LEFT;");
		colOn2.setStyle("-fx-alignment: CENTER-LEFT;");
		
		lvGamesOnline.getColumns().add(colOn0);
		lvGamesOnline.getColumns().add(colOn1);
		lvGamesOnline.getColumns().add(colOn2);
		
		lvGamesOnline.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
			showGame(newValue);
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
	
	public void showGame(GameBase ggame) {
		game = ggame;
		if (game instanceof GameSaved)
			showOfflineGame();
		else
			showOnlineGame((GameOnline) game);
	}
	

	public void showOnlineGame(GameOnline game) {
		WebEngine webEngine = webView.getEngine();
		webEngine.setJavaScriptEnabled(true);
		
		URI uri = URI.create(MainApp.webConnector.getUrlFromGame(game));
		
		List<String> cookies = new ArrayList<>();
		for(Cookie cookie : mainApp.webConnector.cookies.getCookies()) {
			cookies.add(cookie.getName() + "=" + cookie.getValue());
		}
		
		Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
		headers.put("Set-Cookie", cookies);
		try {
			CookieHandler.getDefault().put(uri, headers);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		webEngine.load(uri.toString());
	}
	
	
	public void showOfflineGame() {
		// GameSaved game benutzen
		// in javafx.scene.web.WebView webView darstellen
	}
	

	@FXML void clickStartGameOnline() {
		Task<GameOnline> challenge = new Task<GameOnline>() {
			public GameOnline call() {
				try {
					return MainApp.webConnector.challenge(lvPlayerOnline1.getSelectionModel().getSelectedItem(), lvPlayerOnline2.getSelectionModel().getSelectedItem());
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		};
		
		prStartGameOnline.setVisible(true);
		
		challenge.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			prStartGameOnline.setVisible(false);
			if (newValue == null) {
				Dialog.error("Die Herausforderung ist fehlgeschlagen");
				return;
			}
			MainApp.onlineGames.add(0, newValue);
			lvGamesOnline.getSelectionModel().selectFirst();
		});
		
		Thread thread = new Thread(challenge, "challenge");
		thread.setDaemon(true);
		thread.start();
		
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

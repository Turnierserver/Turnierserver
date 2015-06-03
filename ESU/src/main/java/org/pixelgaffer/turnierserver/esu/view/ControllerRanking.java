package org.pixelgaffer.turnierserver.esu.view;

import org.pixelgaffer.turnierserver.esu.Ai;
import org.pixelgaffer.turnierserver.esu.MainApp;
import org.pixelgaffer.turnierserver.esu.ParticipantResult;
import org.pixelgaffer.turnierserver.esu.Version;
import org.pixelgaffer.turnierserver.esu.utilities.Resources;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControllerRanking {
	

	@FXML Label lbName;
	@FXML Label lbUser;
	@FXML Label lbElo;
	@FXML Label lbLanguage;
	@FXML Button btChallenge;
	@FXML TextArea tbDescription;
	@FXML TableView<Ai> tvAis;
	@FXML TableView<Version> tvVersions;
	@FXML ImageView imageView;
	
	MainApp mainApp;
	public ObservableList<Ai> ais = FXCollections.observableArrayList();
	Ai ai;
	
	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
	public void setMainApp(MainApp app){
		mainApp = app;
		mainApp.cRanking = this;
		tvAis.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ai>() {
		    @Override
		    public void changed(ObservableValue<? extends Ai> observable, Ai oldValue, Ai newValue) {
		        clickChangeAi(newValue);
		    }
		});
		

		TableColumn col0 = new TableColumn("Bild");
		TableColumn col1 = new TableColumn("Name");
		TableColumn col2 = new TableColumn("Besitzer");
		TableColumn col3 = new TableColumn("ELO");

		col0.setCellValueFactory(new PropertyValueFactory<Ai, Image>("onlinePicture"));
		col1.setCellValueFactory(new PropertyValueFactory<Ai, String>("title"));
		col2.setCellValueFactory(new PropertyValueFactory<Ai, StringProperty>("userName"));
		col3.setCellValueFactory(new PropertyValueFactory<Ai, String>("elo"));
		
		loadOnlineAis();
		tvAis.getColumns().addAll(col0, col1, col2, col3);

		TableColumn colV0 = new TableColumn("Version");
		TableColumn colV1 = new TableColumn("Kompiliert");
		TableColumn colV2 = new TableColumn("Qualifiziert");
		TableColumn colV3 = new TableColumn("Fertiggestellt");

		colV0.setCellValueFactory(new PropertyValueFactory<Version, String>("number"));
		colV1.setCellValueFactory(new PropertyValueFactory<Version, String>("compiled"));
		colV2.setCellValueFactory(new PropertyValueFactory<Version, String>("qualified"));
		colV3.setCellValueFactory(new PropertyValueFactory<Version, String>("finished"));

		tvVersions.getColumns().addAll(colV0, colV1, colV2, colV3);
		
	}
	
	public void loadOnlineAis(){
		ais = mainApp.webConnector.getAis(MainApp.actualGameType.get());
		tvAis.setItems(ais);
		tvAis.getSelectionModel().selectFirst();
	}

	public void showAi(Ai aai){
		ai = aai;
		showAi();
	}
	public void showAi(){
		if (ai != null){
			lbName.setText(ai.title);
			tbDescription.setText(ai.description);
			lbUser.setText(ai.userName.get());
			lbElo.setText(ai.elo);
			lbLanguage.setText(ai.language.toString());
			imageView.imageProperty().bindBidirectional(ai.getPicture());
			tvVersions.setItems(ai.versions);
		}
		else{
			lbName.setText("Null");
			tbDescription.setText("Aktuell wird keine KI angezeigt");
			lbUser.setText("Keiner");
			lbElo.setText("1000");
			lbLanguage.setText("Java");
			imageView.imageProperty().set(Resources.defaultPicture());
			tvVersions.setItems(null);
		}
	}
	
	
	@FXML public void clickChallenge(){
		
	}
	
	public void clickChangeAi(Ai selected){
		showAi(selected);
	}
	
	
}

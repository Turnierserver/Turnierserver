package org.pixelgaffer.turnierserver.esu.view;

import org.pixelgaffer.turnierserver.esu.Ai;
import org.pixelgaffer.turnierserver.esu.MainApp;
import org.pixelgaffer.turnierserver.esu.ParticipantResult;
import org.pixelgaffer.turnierserver.esu.Version;
import org.pixelgaffer.turnierserver.esu.utilities.Resources;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

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
		
		loadOnlineAis();
		
		TableColumn<Ai,Image> col0 = new TableColumn<Ai,Image>("Bild");
		TableColumn<Ai,String> col1 = new TableColumn<Ai,String>("Name");
		TableColumn<Ai,String> col2 = new TableColumn<Ai,String>("Besitzer");
		TableColumn<Ai,String> col3 = new TableColumn<Ai,String>("ELO");
		
		col0.setCellValueFactory(new Callback<CellDataFeatures<Ai, Image>, ObservableValue<Image>>() {
			@Override
			public ObservableValue<Image> call(CellDataFeatures<Ai, Image> arg0) {
				return arg0.getValue().getPicture();
			}
		});
		
		col0.setCellValueFactory(new PropertyValueFactory<Ai, Image>("onlinePicture"));
		col0.setCellFactory(new Callback<TableColumn<Ai, Image>, TableCell<Ai, Image>>() {
			@Override
			public TableCell<Ai, Image> call(TableColumn<Ai, Image> param) {
				//Set up the ImageView
				final ImageView imageview = new ImageView();
				imageview.setFitHeight(50);
				imageview.setFitWidth(50);
				
				//Set up the Table
				TableCell<Ai, Image> cell = new TableCell<Ai, Image>() {
					public void updateItem(Image item, boolean empty) {
						if (item != null)
							imageview.imageProperty().set(item);
					}
				};
				
				// Attach the imageview to the cell
				cell.setGraphic(imageview);
				
				
				return cell;
			}
		
		});
		
		
		
		
		
		col1.setCellValueFactory(new Callback<CellDataFeatures<Ai, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Ai, String> p) {
				return new SimpleStringProperty(p.getValue().title);
			}
		});
		col2.setCellValueFactory(new Callback<CellDataFeatures<Ai, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Ai, String> p) {
				return p.getValue().userName;
			}
		});
		col3.setCellValueFactory(new Callback<CellDataFeatures<Ai, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<Ai, String> p) {
				return new SimpleStringProperty(p.getValue().elo);
			}
		});
		
		tvAis.getColumns().add(col0);
		tvAis.getColumns().add(col1);
		tvAis.getColumns().add(col2);
		tvAis.getColumns().add(col3);

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
			btChallenge.setDisable(false);
			imageView.imageProperty().unbind();
			imageView.imageProperty().bind(ai.getPicture());
			tvVersions.setItems(ai.versions);
		}
		else{
			lbName.setText("Null");
			tbDescription.setText("Aktuell wird keine KI angezeigt");
			lbUser.setText("Keiner");
			lbElo.setText("1000");
			lbLanguage.setText("Java");
			btChallenge.setDisable(true);
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

package org.pixelgaffer.turnierserver.esu.view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.pixelgaffer.turnierserver.esu.*;
import org.pixelgaffer.turnierserver.esu.Dialog;
import org.pixelgaffer.turnierserver.esu.Player.NewVersionType;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ControllerAiManagement{
	

	MainApp mainApp;
	@FXML public Button btAbort;
	@FXML public Button btEdit;
	@FXML public Button btNewVersion;
	@FXML public Button btCompile;
	@FXML public Button btQualify;
	@FXML public Button btFinish;
	@FXML public Button btUpload;
	@FXML public Button btToActual;
	@FXML public Button btChangeImage;
	@FXML public Button btDeleteImage;
	@FXML public Label lbName;
	@FXML public Label lbLanguage;
	@FXML public Label lbCompiled;
	@FXML public Label lbQualified;
	@FXML public Label lbFinished;
	@FXML public Label lbUploaded;
	@FXML public RadioButton rbSimple;
	@FXML public RadioButton rbContinue;
	@FXML public RadioButton rbFromFile;
	@FXML public TextField tbFile;
	@FXML public TextField tbName;
	@FXML public TextArea tbOutput;
	@FXML public TextArea tbDescription;
	@FXML public ChoiceBox<Version> cbVersion;
	@FXML public ChoiceBox<String> cbLanguage;
	@FXML public ListView<Player> lvAis;
	@FXML public ImageView image;
	@FXML public TabPane tpCode;
	
	public Player player;
	public Version version;


	public void setMainApp(MainApp app){
		mainApp = app;
		mainApp.cAi = this;
		cbVersion.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Version>() {
		    @Override
		    public void changed(ObservableValue<? extends Version> observable, Version oldValue, Version newValue) {
		        clickVersionChange();
		    }
		});
		lvAis.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Player>() {
		    @Override
		    public void changed(ObservableValue<? extends Player> observable, Player oldValue, Player newValue) {
		        clickChangeAi();
		    }
		});
	}
	
	
	
	public void showPlayers(PlayerManager manager){
		lvAis.setItems(manager.players);
	}
	
	public void showPlayer(Player p, Version v){
		player = p;
		version = v;
		showPlayer();
	}
	public void showPlayer(){
		if (player != null){
			lbName.setText(player.title);
			lbLanguage.setText("Sprache: " + player.language.toString());
			tbDescription.setText(player.description);
			cbVersion.setItems(player.versions);
			image.setImage(player.getPicture());
			
			btChangeImage.setDisable(false);
			btDeleteImage.setDisable(false);
			btNewVersion.setDisable(false);
			btEdit.setDisable(false);
			btToActual.setDisable(false);
			
			if (version == null){  //versuchen, die Version zu setzen, wenn keine ausgewählt ist
				version = player.lastVersion();
			}
			boolean containing = false;
			for (int i = 0; i < player.versions.size(); i++)
				if (version == player.versions.get(i))
					containing = true;
			if(!containing){  //oder eine nicht-zugehörige ausgewählt ist
				version = player.lastVersion();
			}
		}
		else{
			lbName.setText("Name");
			lbLanguage.setText("Sprache: ");
			tbDescription.setText("Momentan ist kein Spieler ausgewählt");
			ObservableList<Version> emptyFill = FXCollections.observableArrayList();
			cbVersion.setItems(emptyFill);
			image.setImage(Resources.defaultPicture());

			btChangeImage.setDisable(true);
			btDeleteImage.setDisable(true);
			btNewVersion.setDisable(true);
			btEdit.setDisable(true);
			btToActual.setDisable(true);
		}
		
		//Beschreibung setzen
		tbDescription.setEditable(false);
		btAbort.setVisible(false);
		btEdit.setText("Bearbeiten");
		tbDescription.setEditable(false);
		
		
		if (version != null && player != null){
			cbVersion.setValue(version);
			tbOutput.setText("");
			if (version.compiled){
				tbOutput.setText(version.compileOutput);
			}
			if (version.qualified){
				tbOutput.setText(version.qualifyOutput);
			}
			lbCompiled.setVisible(version.compiled);
			lbQualified.setVisible(version.qualified);
			lbFinished.setVisible(version.finished);
			lbUploaded.setVisible(version.uploaded);
			btCompile.setDisable(version.compiled);
			btQualify.setDisable(version.qualified);
			btFinish.setDisable(version.finished);
			btUpload.setDisable(false);
			rbContinue.setDisable(false);

			rbContinue.setSelected(true);
			rbFromFile.setSelected(false);
			rbSimple.setSelected(false);
		}
		else{
			cbVersion.setValue(null);
			tbOutput.setText("");
			lbCompiled.setVisible(false);
			lbQualified.setVisible(false);
			lbFinished.setVisible(false);
			lbUploaded.setVisible(false);
			btCompile.setDisable(true);
			btQualify.setDisable(true);
			btFinish.setDisable(true);
			btUpload.setDisable(true);
			rbContinue.setDisable(true);
			
			rbContinue.setSelected(false);
			rbFromFile.setSelected(false);
			rbSimple.setSelected(true);
		}
	}
	
	
	
	
	
	@FXML void clickNewAi(){
		tbFile.setText("Info1 geklickt");
	}

	@FXML void clickChangeAi(){
		player = lvAis.getSelectionModel().getSelectedItem();
		version = player.lastVersion();
		showPlayer();
	}
	
	@FXML void clickAbort(){
		btAbort.setVisible(false);
		btEdit.setText("Bearbeiten");
		tbDescription.setEditable(false);
		tbDescription.setText(player.description);
	}
	
	@FXML void clickEdit(){
		if (!btAbort.isVisible()){
			btAbort.setVisible(true);
			btEdit.setText("Speichern");
			tbDescription.setEditable(true);
		}
		else{
			btAbort.setVisible(false);
			btEdit.setText("Bearbeiten");
			tbDescription.setEditable(false);
			player.setDescription(tbDescription.getText());
		}
	}
	
	@FXML void clickToActual(){
		version = player.lastVersion();
		showPlayer();
	}
	
	@FXML void clickVersionChange(){
		if (version != cbVersion.getValue()){
			version = cbVersion.getValue();
			showPlayer();
		}
	}
	
	@FXML void clickRbSimple(){
		rbSimple.setSelected(true);
		rbContinue.setSelected(false);
		rbFromFile.setSelected(false);
	}
	
	@FXML void clickRbContinue(){
		rbSimple.setSelected(false);
		rbContinue.setSelected(true);
		rbFromFile.setSelected(false);
	}
	
	@FXML void clickRbFromFile(){
		rbSimple.setSelected(false);
		rbContinue.setSelected(false);
		rbFromFile.setSelected(true);
	}
	
	@FXML void clickSelectFile(){
		tbFile.setText(Dialog.folderChooser(mainApp.stage, "Bitte einen Ordner auswählen").getPath());
	}
	
	@FXML void clickNewVersion(){
		if (rbFromFile.isSelected()){
			showPlayer(player, player.newVersion(NewVersionType.fromFile, tbFile.getText()));
		}
		else if (rbContinue.isSelected()){
			showPlayer(player, player.newVersion(NewVersionType.lastVersion));
		}
		else{
			showPlayer(player, player.newVersion(NewVersionType.simplePlayer));
		}
	}
	
	@FXML void clickCompile(){
		version.compile();
		showPlayer();
	}
	
	@FXML void clickQualify(){
		version.qualify();
		showPlayer();
	}
	
	@FXML void clickFinish(){
		if (Dialog.okAbort("Wenn eine Version fertiggestellt wird, kann sie nicht mehr bearbeitet werden.\n\nFortfahren?", "Version einfrieren")){
			version.finish();
		}
		showPlayer();
	}
	
	@FXML void clickUpload(){
		tbFile.setText("Info14 geklickt");
	}
	
	@FXML void clickChangeImage(){
		File result = Dialog.fileChooser(mainApp.stage, "Bild auswählen");
		Image img = Resources.imageFromFile(result);
		if (img != null){
			player.setPicture(Resources.imageFromFile(result));
		}
		showPlayer();
	}

	@FXML void clickDeleteImage(){
		player.setPicture(null);
		showPlayer();
	}
	
}

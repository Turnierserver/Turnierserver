package org.pixelgaffer.turnierserver.esu.view;

import java.io.File;

import org.pixelgaffer.turnierserver.esu.*;
import org.pixelgaffer.turnierserver.esu.Player.Language;
import org.pixelgaffer.turnierserver.esu.Player.NewVersionType;
import org.pixelgaffer.turnierserver.esu.Player.PlayerMode;
import org.pixelgaffer.turnierserver.esu.utilities.Dialog;
import org.pixelgaffer.turnierserver.esu.utilities.Paths;
import org.pixelgaffer.turnierserver.esu.utilities.Resources;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

public class ControllerAiManagement{
	

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
	@FXML public ChoiceBox<Language> cbLanguage;
	@FXML public ListView<Player> lvAis;
	@FXML public ImageView image;
	@FXML public TabPane tpCode;
	@FXML public Hyperlink hlShowQualified;
	public Tab infoTab;
	public Tab newFileTab;
	
	public MainApp mainApp;
	public Player player;
	public Version version;

	/**
	 * Initialisiert den Controller
	 * 
	 * @param app eine Referenz auf die MainApp
	 */
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
		tpCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
		    @Override
		    public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
		    	clickTabSelection(oldValue, newValue);
		    }
		});
		cbLanguage.itemsProperty().get().add(Language.Java);
		cbLanguage.itemsProperty().get().add(Language.Python);
		cbLanguage.getSelectionModel().selectFirst();
		
		infoTab = tpCode.getTabs().get(0);
		newFileTab = tpCode.getTabs().get(1);
		mainApp.playerManager.loadPlayers();
		showPlayers();
	}
	
	
	/**
	 * Lädt alle KIs in die KI-Liste
	 */
	public void showPlayers(){
		lvAis.setItems(mainApp.playerManager.players);
		try {
			lvAis.getSelectionModel().selectFirst();
		} catch (Exception e) {}
	}
	
	/**
	 * Zeigt eine KI und eine ihrer Versionen an
	 * 
	 * @param p die KI
	 * @param v die zugeh�rige Version
	 */
	public void showPlayer(Player p, Version v){
		player = p;
		version = v;
		showPlayer();
	}
	/**
	 * Setzt alle Eigenschaften der Benutzeroberfläche, wie z.B. das KI-Namensfeld, das KI-Bild, die KI-Beschreibung, ...
	 */
	public void showPlayer(){
		
		// Player-spezifisches
		if (player != null){
			lbName.setText(player.title);
			lbLanguage.setText("Sprache: " + player.language.toString());
			tbDescription.setText(player.description);
			cbVersion.getSelectionModel().clearSelection();
			cbVersion.setItems(player.versions);
			image.setImage(player.getPicture());
			
			btChangeImage.setDisable(false);
			btDeleteImage.setDisable(false);
			btNewVersion.setDisable(false);
			btEdit.setDisable(false);
			btToActual.setDisable(false);
			
			if (version == null){  //versuchen, die Version zu setzen, wenn keine ausgew�hlt ist
				version = player.lastVersion();
			}
			boolean containing = false;
			for (int i = 0; i < player.versions.size(); i++)
				if (version == player.versions.get(i))
					containing = true;
			if(!containing){  //oder eine nicht-zugeh�rige ausgew�hlt ist
				version = player.lastVersion();
			}
		}
		else{
			lbName.setText("Name");
			lbLanguage.setText("Sprache: ");
			tbDescription.setText("Momentan ist kein Spieler ausgew�hlt");
			cbVersion.getSelectionModel().clearSelection();
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
		
		//Version-spezifisches
		if (version != null && player != null){
			cbVersion.setValue(version);
			tbOutput.setText("");
			if (!version.compileOutput.equals("")){
				tbOutput.setText(version.compileOutput);
			}
			if (!version.qualifyOutput.equals("")){
				tbOutput.setText(version.qualifyOutput);
			}
			lbCompiled.setVisible(version.compiled);
			hlShowQualified.setVisible(version.qualified);
			lbFinished.setVisible(version.finished);
			lbUploaded.setVisible(version.uploaded);
			btCompile.setDisable(version.compiled || version.finished);
			btQualify.setDisable(version.qualified || !version.compiled || version.finished);
			btFinish.setDisable(version.finished);
			btUpload.setDisable(false);
			rbContinue.setDisable(false);

			rbContinue.setSelected(true);
			rbFromFile.setSelected(false);
			rbSimple.setSelected(false);
			
			setVersionTabs();
		}
		else{
			cbVersion.setValue(null);
			tbOutput.setText("");
			lbCompiled.setVisible(false);
			hlShowQualified.setVisible(false);
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
		
		if (player != null){
			if (player.mode == PlayerMode.simplePlayer){
				btEdit.setDisable(true);
				btNewVersion.setDisable(true);
				btCompile.setDisable(true);
				btQualify.setDisable(true);
				btFinish.setDisable(true);
				btUpload.setDisable(true);
				lbCompiled.setVisible(false);
				hlShowQualified.setVisible(false);
				lbFinished.setVisible(false);
				lbUploaded.setVisible(false);
				
				btChangeImage.setDisable(true);
				btDeleteImage.setDisable(true);
			}
		}
	}
	
	/**
	 * Lädt mithilfe der CodeEditoren der anzuzeigenden Version alle Dateien der Version in die Tab-Leiste
	 */
	private void setVersionTabs(){
		tpCode.getTabs().clear();
		tpCode.getTabs().add(infoTab);
		for (int i = 0; i < version.files.size(); i++){
			version.files.get(i).load();
			tpCode.getTabs().add(version.files.get(i).getView());
		}
		tpCode.getTabs().add(newFileTab);
	}
	
	/**
	 * Speichert und überprüft, ob auf das "neue Datei"-Tab geklickt wurde
	 * 
	 * @param oldTab der zuvor ausgewählte Tab
	 * @param newTab der neu ausgewählte Tab
	 */
	void clickTabSelection(Tab oldTab, Tab newTab){
    	if (version != null)
    		version.saveCode();
    	if (newTab == newFileTab && newTab != oldTab){
    		tpCode.getSelectionModel().select(oldTab);
    		if (version == null){
    			Dialog.error("Bitte legen Sie zuerst eine Version an.", "Keine Version");
    		}
    		File result = Dialog.fileSaver(mainApp.stage, "Bitte einen Ort und Dateinamen auswählen", Paths.version(version));
    		if (result != null){
    			CodeEditor editor = new CodeEditor(result);
    			editor.forceSave();
    			version.files.add(editor);
    			tpCode.getTabs().add(tpCode.getTabs().size()-1, editor.getView());
    			tpCode.getSelectionModel().select(tpCode.getTabs().size()-2);
    		}
    	}
	}
		
	/**
	 * Button: Neue KI anlegen
	 */
	@FXML void clickNewAi(){
		String title = tbName.getText().replace(" ", "");
		
		if (title.equals("")){
			Dialog.error("Bitte einen Namen für die KI eingeben", "Kein Name");
			return;
		}
		
		for (int i = 0; i < lvAis.getItems().size(); i++){  //Testen, ob die KI schon existiert
			if (title.equals(lvAis.getItems().get(i).title)){
				Dialog.error("Es können keine zwei KIs mit dem gleichen Namen erstellt werden", "Doppelter Name");
				return;
			}
		}
		
		mainApp.playerManager.players.add(new Player(title, cbLanguage.getValue()));
		lvAis.getSelectionModel().selectLast();
	}

	/**
	 * Listenselektions-Änderung: zeigt andere KI an
	 */
	@FXML void clickChangeAi(){
		player = lvAis.getSelectionModel().getSelectedItem();
		version = player.lastVersion();
		showPlayer();
	}
	
	/**
	 * Button: Abbruch der Bearbeitung der Beschreibung der KI
	 */
	@FXML void clickAbort(){
		btAbort.setVisible(false);
		btEdit.setText("Bearbeiten");
		tbDescription.setEditable(false);
		tbDescription.setText(player.description);
	}
	
	/**
	 * Button: Bearbeitung der Beschreibung der KI
	 */
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
	
	/**
	 * Button: aktuelle Version der KI wird ausgewählt
	 */
	@FXML void clickToActual(){
		version = player.lastVersion();
		showPlayer();
	}
	
	/**
	 * Listenselektions-Änderung: zeigt andere Version an
	 */
	@FXML void clickVersionChange(){
		if (version != cbVersion.getValue() && cbVersion.getValue() != null){
			version = cbVersion.getValue();
			showPlayer();
		}
	}
	
	/**
	 * Radiobutton: "SimplePlayer" wurde ausgewählt
	 */
	@FXML void clickRbSimple(){
		rbSimple.setSelected(true);
		rbContinue.setSelected(false);
		rbFromFile.setSelected(false);
	}
	
	/**
	 * Radiobutton: "Weiterschreiben" wurde ausgewählt
	 */
	@FXML void clickRbContinue(){
		rbSimple.setSelected(false);
		rbContinue.setSelected(true);
		rbFromFile.setSelected(false);
	}
	
	/**
	 * Radiobutton: "Aus Datei" wurde ausgewählt
	 */
	@FXML void clickRbFromFile(){
		rbSimple.setSelected(false);
		rbContinue.setSelected(false);
		rbFromFile.setSelected(true);
	}
	
	/**
	 * Button: Dateiauswahl wenn "Aus Datei" ausgew�hlt ist
	 */
	@FXML void clickSelectFile(){
		tbFile.setText(Dialog.folderChooser(mainApp.stage, "Bitte einen Ordner ausw�hlen").getPath());
	}
	
	/**
	 * Button: neue Version erstellen
	 */
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
	
	/**
	 * Button: Kompilieren
	 */
	@FXML void clickCompile(){
		version.compile();
		showPlayer();
	}
	
	/**
	 * Button: Qualifizieren
	 */
	@FXML void clickQualify(){
		version.qualify();
		showPlayer();
	}
	
	/**
	 * Button: Fertigstellen
	 */
	@FXML void clickFinish(){
		if (Dialog.okAbort("Wenn eine Version fertiggestellt wird, kann sie nicht mehr bearbeitet werden.\n\nFortfahren?", "Version einfrieren")){
			version.finish();
		}
		showPlayer();
	}
	
	/**
	 * Button: Hochladen
	 */
	@FXML void clickUpload(){
		tbFile.setText("Info14 geklickt");
	}

	/**
	 * Hyperlink: zeigt das Qualifizier-Spiel an
	 */
	@FXML void clickShowQualified(){
		tbFile.setText("Info14 geklickt");
	}
	
	/**
	 * Button: Bild ändern
	 */
	@FXML void clickChangeImage(){
		File result = Dialog.fileChooser(mainApp.stage, "Bild ausw�hlen");
		Image img = Resources.imageFromFile(result);
		if (img != null){
			player.setPicture(Resources.imageFromFile(result));
		}
		showPlayer();
	}

	/**
	 * Button: Bild löschen
	 */
	@FXML void clickDeleteImage(){
		player.setPicture(null);
		showPlayer();
	}
	
}

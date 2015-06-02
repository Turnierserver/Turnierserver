package org.pixelgaffer.turnierserver.esu.view;

import java.io.File;
import java.io.IOException;

import org.pixelgaffer.turnierserver.esu.*;
import org.pixelgaffer.turnierserver.esu.Ai.NewVersionType;
import org.pixelgaffer.turnierserver.esu.Ai.AiMode;
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
	@FXML public ChoiceBox<String> cbLanguage;
	@FXML public ListView<Ai> lvAis;
	@FXML public ImageView image;
	@FXML public TabPane tpCode;
	@FXML public Hyperlink hlShowQualified;
	public Tab infoTab;
	public Tab newFileTab;
	
	public MainApp mainApp;
	public Ai ai;
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
		lvAis.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ai>() {
		    @Override
		    public void changed(ObservableValue<? extends Ai> observable, Ai oldValue, Ai newValue) {
		        clickChangeAi();
		    }
		});
		tpCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
		    @Override
		    public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
		    	clickTabSelection(oldValue, newValue);
		    }
		});
		for (int i = 0; i < MainApp.languages.size(); i++){
			cbLanguage.getItems().add(MainApp.languages.get(i));
		}
		cbLanguage.getSelectionModel().selectFirst();
		
		infoTab = tpCode.getTabs().get(0);
		newFileTab = tpCode.getTabs().get(1);
		mainApp.aiManager.loadPlayers();
		showAis();
	}
	
	
	/**
	 * Lädt alle KIs in die KI-Liste
	 */
	public void showAis(){
		lvAis.setItems(mainApp.aiManager.ais);
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
	public void showAi(Ai p, Version v){
		ai = p;
		version = v;
		showAi();
	}
	/**
	 * Setzt alle Eigenschaften der Benutzeroberfläche, wie z.B. das KI-Namensfeld, das KI-Bild, die KI-Beschreibung, ...
	 */
	public void showAi(){
		
		// Ai-spezifisches
		if (ai != null){
			lbName.setText(ai.title);
			lbLanguage.setText("Sprache: " + ai.language.toString());
			tbDescription.setText(ai.description);
			cbVersion.getSelectionModel().clearSelection();
			cbVersion.setItems(ai.versions);
			image.setImage(ai.getPicture());
			
			btChangeImage.setDisable(false);
			btDeleteImage.setDisable(false);
			btNewVersion.setDisable(false);
			btEdit.setDisable(false);
			btToActual.setDisable(false);
			
			if (version == null){  //versuchen, die Version zu setzen, wenn keine ausgew�hlt ist
				version = ai.lastVersion();
			}
			boolean containing = false;
			for (int i = 0; i < ai.versions.size(); i++)
				if (version == ai.versions.get(i))
					containing = true;
			if(!containing){  //oder eine nicht-zugeh�rige ausgew�hlt ist
				version = ai.lastVersion();
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
		if (version != null && ai != null){
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
		
		if (ai != null){
			if (ai.mode == AiMode.simplePlayer){
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
		
		mainApp.aiManager.ais.add(new Ai(title, cbLanguage.getValue()));
		lvAis.getSelectionModel().selectLast();
	}

	/**
	 * Listenselektions-Änderung: zeigt andere KI an
	 */
	@FXML void clickChangeAi(){
		ai = lvAis.getSelectionModel().getSelectedItem();
		version = ai.lastVersion();
		showAi();
	}
	
	/**
	 * Button: Abbruch der Bearbeitung der Beschreibung der KI
	 */
	@FXML void clickAbort(){
		btAbort.setVisible(false);
		btEdit.setText("Bearbeiten");
		tbDescription.setEditable(false);
		tbDescription.setText(ai.description);
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
			ai.setDescription(tbDescription.getText());
		}
	}
	
	/**
	 * Button: aktuelle Version der KI wird ausgewählt
	 */
	@FXML void clickToActual(){
		version = ai.lastVersion();
		showAi();
	}
	
	/**
	 * Listenselektions-Änderung: zeigt andere Version an
	 */
	@FXML void clickVersionChange(){
		if (version != cbVersion.getValue() && cbVersion.getValue() != null){
			version = cbVersion.getValue();
			showAi();
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
			showAi(ai, ai.newVersion(NewVersionType.fromFile, tbFile.getText()));
		}
		else if (rbContinue.isSelected()){
			showAi(ai, ai.newVersion(NewVersionType.lastVersion));
		}
		else{
			showAi(ai, ai.newVersion(NewVersionType.simplePlayer));
		}
	}
	
	/**
	 * Button: Kompilieren
	 */
	@FXML void clickCompile(){
		version.compile();
		showAi();
	}
	
	/**
	 * Button: Qualifizieren
	 */
	@FXML void clickQualify(){
		version.qualify();
		showAi();
	}
	
	/**
	 * Button: Fertigstellen
	 */
	@FXML void clickFinish(){
		if (Dialog.okAbort("Wenn eine Version fertiggestellt wird, kann sie nicht mehr bearbeitet werden.\n\nFortfahren?", "Version einfrieren")){
			version.finish();
		}
		showAi();
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
			ai.setPicture(Resources.imageFromFile(result));
		}
		showAi();
	}

	/**
	 * Button: Bild löschen
	 */
	@FXML void clickDeleteImage(){
		ai.setPicture(null);
		showAi();
	}
	
}

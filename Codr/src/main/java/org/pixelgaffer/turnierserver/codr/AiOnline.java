package org.pixelgaffer.turnierserver.codr;


import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.utilities.Paths;
import org.pixelgaffer.turnierserver.codr.utilities.Resources;
import org.pixelgaffer.turnierserver.codr.utilities.WebConnector;


/**
 * Stellt eine Online-KI dar, die auf dem Turnierserver existiert.
 * Dabei wird nichts im Dateisystem gespeichert.
 * 
 * @author Philip
 */
public class AiOnline extends AiBase {
	
	public String userName = "";
	public int id;
	public int elo = -1;
	private ObjectProperty<Image> picture = new SimpleObjectProperty<Image>();
	public ObservableList<OnlineGameInfo> onlineGamesInfos = FXCollections.observableArrayList();
	
	
	public AiOnline(JSONObject json, WebConnector connector) {
		this(json, connector, null);
	}
	
	/**
	 * @param games 
	 * Erstellt eine neue Online-Ai aus einem JSONObject
	 * 
	 * @param json das übergebene JSONObject
	 * @param loadGames true wenn die Spiele geladen werden sollen
	 * @throws
	 */
	public AiOnline(JSONObject json, WebConnector connector, JSONArray games) {
		super(json.getString("name"), AiMode.online);
		
		userName = json.getString("author");
		if (!json.isNull("description"))
			description = json.getString("description");
		gametype = json.getJSONObject("gametype").getString("name");
		language = json.getJSONObject("lang").getString("name");
		id = json.getInt("id");
		elo = (int) Math.round(json.getDouble("elo"));
		JSONArray versions = json.getJSONArray("versions");
		for (int i = 0; i < versions.length(); i++) {
			newVersion(versions.getJSONObject(i));
		}
		
		for(int i = 0; games != null && i < games.length(); i++) {
			JSONObject game = games.getJSONObject(i);
			if(game.getJSONArray("ais").getJSONObject(0).getInt("id") == id || game.getJSONArray("ais").getJSONObject(1).getInt("id") == id) {
				onlineGamesInfos.add(new OnlineGameInfo(game, id));
			}
		}
		
		Task<Image> imageLoader = new Task<Image>() {
			
			public Image call() {
				try {
					Image img = connector.getImage(json.getInt("id"));
					if (img == null) {
						img = connector.getImage(json.getInt("id")); // zweiter Versuch (das geht aber auch elleganter :D)
						if (img == null) {
							return Resources.defaultPicture();
						} else
							return img;
					} else
						return img;
				} catch (IOException e) {
					return Resources.defaultPicture();
				}
			}
		};
		
		imageLoader.valueProperty().addListener((observableValue, oldValue, newValue) -> {
			setPicture(newValue);
		});
		
		Thread thread = new Thread(imageLoader, "imageLoader");
		thread.setDaemon(true);
		thread.start();
		
	}
	
	
	/**
	 * Fügt eine neue Version, die mit JSON erstellt wurde, der Versionsliste hinzu.
	 * 
	 * @param json hieraus wird die Version erstellt
	 * @return die Version, die erstellt wurde
	 */
	public Version newVersion(JSONObject json) {
		Version version = new Version(this, json.getInt("id") - 1, json);
		versions.add(version);
		return version;
	}
	
	
	/**
	 * Gibt das gespeicherte Bild des Spielers zurück.
	 * 
	 * @return das gespeicherte Bild
	 */
	public ObjectProperty<Image> getPicture() {
		if (mode == AiMode.online) {
			if (picture != null) {
				return picture;
			} else {
				return new SimpleObjectProperty<>(Resources.defaultPicture());
			}
		}
		ObjectProperty<Image> img = new SimpleObjectProperty<>(Resources.imageFromFile(Paths.aiPicture(this)));
		if (img.get() == null) {
			img = new SimpleObjectProperty<>(Resources.defaultPicture());
		}
		return img;
	}
	
	
	/**
	 * Speichert das Bild des Spielers in der Datei picture.png.
	 * 
	 * @param img das zu speichernde Bild
	 */
	@Override
	public void setPicture(Image img) {
		if (img == null)
			picture.set(Resources.defaultPicture());
		else
			picture.set(img);
	}
	
	
}

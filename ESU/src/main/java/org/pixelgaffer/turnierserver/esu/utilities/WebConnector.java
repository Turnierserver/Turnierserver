package org.pixelgaffer.turnierserver.esu.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.esu.Ai;
import org.pixelgaffer.turnierserver.esu.Game;
import org.pixelgaffer.turnierserver.esu.Version;

public class WebConnector {
	
	private final String url;
	private final String cookieUrl;
	
	private CookieStore cookies = new BasicCookieStore();
	private CloseableHttpClient http = HttpClients.custom().setDefaultCookieStore(cookies).build();
	
	/**
	 * Erstellt einen neuen Web Connector
	 * 
	 * @param url Die URL der API (z.B. http://www.thuermchen.com/api/ <- Der '/' muss da sein)
	 */
	public WebConnector(final String url, final String cookieUrl){
		this.url = url;
		this.cookieUrl = cookieUrl;
		readFromFile();
	}
	
	/**
	 * Loggt den Benutzer ein
	 * 
	 * @param username Der Benutzername
	 * @param password Der Passwort
	 * @return Gibt an ob das Login erfolgreich war
	 * @throws IOException 
	 */
	public boolean login(String username, String password) throws IOException{
		boolean result = sendPost("login", "email", username, "password", password, "remember", "true") != null;
		saveToFile();
		return result;
	}
	
	public boolean register(String username, String firstname, String lastname, String email, String password) throws IOException {
		return sendPost("register", "username", username, "email", email, "password", password, "firstname", firstname, "lastname", lastname) != null;
	}
	
	/**
	 * Loggt den Benutzer aus
	 * 
	 * @return Gibt an ob das Ausloggen erfolgreich war
	 * @throws IOException
	 */
	public boolean logout() throws IOException {
		boolean result = sendPost("logout") != null;
		saveToFile();
		return result;
	}
	
	/**
	 * Gibt zurück ob die ESU momentan eingeloggt ist
	 * 
	 * @return Ob die ESU momentan eingeloggt ist
	 * @throws IOException
	 */
	public boolean isLoggedIn() {
		try {
			if(getSession() == null || getRememberToken() == null) {
				return false;
			}
			boolean result = sendPost("loggedin") != null;
			if(!result) {
				setTokens(null, null);
			}
			return result;
		} catch (IOException e) {
			Dialog.error("Fehler bei der Abfrage des Loginstatuses.");
			ErrorLog.write("Fehler bei der Abfrage des Loginstatuses: " + e.getLocalizedMessage());
			return false;
		}
	}
	
	public ObservableList<Ai> getAis() {
		ObservableList<Ai> result = FXCollections.observableArrayList();
		String json;
		try {
			json = sendGet("ais");
		} catch (IOException e) {
			return result;
		}
		if(json == null) {
			return result;
		}
		JSONArray ais = new JSONArray(json);
		
		for(int i = 0; i < ais.length(); i++) {
			result.add(new Ai(ais.getJSONObject(i), this));
		}
		
		return result;
	}
	
	/**
	 * Gibt das Bild einer AI zurück
	 * 
	 * @param id Die id der AI
	 * @return Das Bild der AI
	 * @throws IOException
	 */
	public Image getImage(int id) throws IOException {
		return new Image(sendGet("ai/" + id + "/icon"));
	}
	
	public List<Game> getGames() {
		throw new UnsupportedOperationException("Ich bin so pöse!");
	}
	
	public void uploadVersion(Version version) {
		
	}
	
	/**
	 * Pingt den Server
	 * 
	 * @return Ob der Server erreichbar ist
	 */
	public boolean ping() {
		try {
			String result = sendGet(null);
			return result != null && result.equals("PONG!");
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Holt sich alle Sprachen. Es wird eine gespeicherte Liste zurückgegeben wenn keine Internetverbindung besteht. Falls keine Liste gespeichert ist, wird null zurückgegeben
	 * 
	 */
	public List<String> getLanguages() {
		List<String> result = new ArrayList<String>();
		
		String json = null;
		try {
			json = sendGet("langs");
		} catch (IOException e) {
			ErrorLog.write("Die Sprachen konnten nicht heruntergeladen werden: " + e.getLocalizedMessage());
		}
		
		if(json == null) {
			try {
				result.addAll(FileUtils.readLines(new File(Paths.langsFile())));
			} catch (IOException e) {
				ErrorLog.write("Die Sprachen konnten nicht aus der Datei werden: " + e.getLocalizedMessage());
				return null;
			}
			return result;
		}
		
		JSONArray langs = new JSONArray(json);
		for(int i = 0; i < langs.length(); i++) {
			result.add(langs.getJSONObject(i).getString("name"));
		}
		
		File langsFile = new File(Paths.gameTypesFile());
		langsFile.delete();
		try {
			langsFile.getParentFile().mkdirs();
			langsFile.createNewFile();
			for(int i = 0; i < result.size(); i++) {
				FileUtils.write(langsFile, result.get(i) + "\n", true);
			}
		} catch (IOException e) {
			ErrorLog.write("Die Sprachen konnten nicht in die Datei geschrieben werden!");
		}
		
		return result;
	}
	
	/**
	 * Holt sich alle Spieltypen. Es wird eine gespeicherte Liste zurückgegeben wenn keine Internetverbindung besteht. Falls keine Liste gespeichert ist, wird null zurückgegeben
	 * 
	 */
	public List<String> getGametypes() {
		List<String> result = new ArrayList<String>();
		
		String json = null;
		try {
			json = sendGet("gametypes");
		} catch (IOException e) {
			ErrorLog.write("Die Spieltypen konnten nicht heruntergeladen werden: " + e.getLocalizedMessage());
		}
		
		if(json == null) {
			try {
				for(String line : FileUtils.readLines(new File(Paths.gameTypesFile()))) {
					result.add(line.split("->")[0]);
				}
			} catch (IOException e) {
				ErrorLog.write("Die Spieltypen konnten nicht aus der Datei geladen werden: " + e.getLocalizedMessage());
				return null;
			}
			return result;
		}
		
		JSONArray gametypes = new JSONArray(json);
		
		List<String> fileLines = new ArrayList<>();
		try {
			fileLines = FileUtils.readLines(new File(Paths.langsFile()));
		} catch (IOException e) {
			ErrorLog.write("Die Spieltypen konnten nicht aus der Datei gelesen werden: " + e.getLocalizedMessage());
			ErrorLog.write("Es werden nun alle Spieltypen geladen werden!");
		}
		
		String[] lines = new String[gametypes.length()];
		
		for(int i = 0; i < gametypes.length(); i++) {
			JSONObject gametype = gametypes.getJSONObject(i);
			String apparentLine = gametype.getString("name") + "->" + gametype.getLong("last_modified");
			
			if(!fileLines.contains(apparentLine)) {
				if(!loadGamelogic(gametype.getInt("id"), gametype.getString("name")) || !loadDataContainer(gametype.getInt("id"), gametype.getString("name"))) {
					ErrorLog.write("Konnte Spiel " + gametype.getInt("id") + " nicht aktualisieren!");
				}
				else {
					lines[gametype.getInt("id") - 1] = apparentLine;
				}
			}
			else {
				lines[gametype.getInt("id") - 1] = apparentLine;
			}
		}
				
		//Speichern in der Datei
		try {
			File gametypesFile = new File(Paths.gameTypesFile());
			gametypesFile.delete();
			gametypesFile.getParentFile().mkdirs();
			gametypesFile.createNewFile();
			for(String line : lines) {
				if(line != null) {
					result.add(line.split("->")[0]);
					FileUtils.write(gametypesFile, line + System.lineSeparator(), true);	
				}
			}
		} catch (IOException e) {
			ErrorLog.write("Die Sprachen konnten nicht in die Datei geschrieben werden!");
		}
		
		return result;
	}
	
	public boolean loadGamelogic(int game, String gameName) {
		String logic;
		try {
			logic = sendGet("gamelogic/" + game);
		} catch (IOException e) {
			ErrorLog.write("Spiellogik konnte nicht heruntergeladen werden: " + e.getLocalizedMessage());
			return false;
		}
		
		if(logic == null) {
			return false;
		}
		
		try {
			FileUtils.write(new File(Paths.gameLogic("gameName")), logic);
		} catch (IOException e) {
			ErrorLog.write("Spiellogik konnte nicht gespeichert werden: " + e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	public boolean loadDataContainer(int game, String gameName) {
		String libraries;
		try {
			libraries = sendGet("data_container/" + game);
		} catch (IOException e) {
			ErrorLog.write("Der Data Container konnten nicht heruntergeladen werden: " + e.getLocalizedMessage());
			return false;
		}
		
		if(libraries == null) {
			return false;
		}
		
		try {
			File tempZip = File.createTempFile("datacontainer", System.currentTimeMillis() + "");
			FileUtils.write(tempZip, libraries);
			ZipFile zipFile = new ZipFile(tempZip);
			File zip;
			zipFile.extractAll((zip = File.createTempFile("datacontainer-unzipped", System.currentTimeMillis() + "")).getAbsolutePath());
			for(File file : new File(zip, "AiLibraries").listFiles()) {
				if(file.isFile()) {
					continue;
				}
				File target = new File(Paths.ailibrary(gameName, file.getName()));
				target.mkdirs();
				FileUtils.copyDirectory(file, target);
			}
			for(File file : new File(zip, "SimplePlayers").listFiles()) {
				if(file.isFile()) {
					continue;
				}
				File target = new File(Paths.simplePlayer(gameName, file.getName()) + "/v0/src");
				target.mkdirs();
				//TODO properties schreiben
				FileUtils.copyDirectory(file, target);
			}
		} catch (IOException | ZipException e) {
			ErrorLog.write("Ai Libraries konnte nicht entpackt werden: " + e.getLocalizedMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Setzt die Tokens einer Session
	 * 
	 * @param rememberToken Den Remember Token
	 * @param sessionToken Den Session Token
	 */
	public void setTokens(String rememberToken, String sessionToken) {
		if(rememberToken == null || sessionToken == null) {
			cookies.clear();
			return;
		}
		cookies.addCookie(createCookie("remember_token", rememberToken));
		cookies.addCookie(createCookie("session", sessionToken));
	}
	
	/**
	 * Gibt den Session Token zurück
	 * 
	 * @return Den Session Token der Session
	 */
	public String getSession() {
		List<Cookie> cookie = cookies.getCookies().stream().filter((Cookie o) -> o.getName().equals("session")).collect(Collectors.toList());
		return cookie.isEmpty() ? null : cookie.get(0).getValue();
	}
	
	/**
	 * Gibt den Remember Token zurück
	 * 
	 * @return Den Remember Token der Session
	 */
	public String getRememberToken() {
		List<Cookie> cookie = cookies.getCookies().stream().filter((Cookie o) -> o.getName().equals("remember_token")).collect(Collectors.toList());
		return cookie.isEmpty() ? null : cookie.get(0).getValue();
	}
	
	/**
	 * Speichert die Session in eine Datei
	 */
	public void saveToFile() {
		File file = new File(Paths.sessionFile());
		try {
			file.createNewFile();
			FileUtils.write(file, (getSession() == null ? "" : getSession()) + System.lineSeparator() + (getRememberToken() == null ? "" : getRememberToken()), false);
		} catch (IOException e) {
			ErrorLog.write("ERROR SAVING SESSION: " + e.getMessage());
			return;
		}
	}
	
	/**
	 * Holt die Session aus einer Datei
	 */
	public void readFromFile() {
		File file = new File(Paths.sessionFile());
		try {
			if(!file.exists()) {
				return;
			}
			String[] tokens = FileUtils.readFileToString(file).split("\n");
			if(tokens.length != 2) {
				return;
			}
			setTokens(tokens[1].isEmpty() ? null : tokens[1], tokens[0].isEmpty() ? null : tokens[0]);
		} catch (IOException e) {
			ErrorLog.write("ERROR SAVING SESSION: " + e.getMessage());
			return;
		}
	}
	
	public String sendPost(String command) throws IOException {
		return sendPost(command, new NameValuePair[0]);
	}
	
	public String sendPost(String command, String...data) throws IOException {
		if(data.length % 2 != 0) {
			throw new IllegalArgumentException("Pöse pöse, data muss immer eine Länge % 2 = 0 haben!");
		}
		NameValuePair[] nvpData = new BasicNameValuePair[data.length / 2];
		for(int i = 0; i < nvpData.length; i++) {
			if(data[i * 2] != null && data[i * 2 + 1] != null) {
				nvpData[i] = new BasicNameValuePair(data[i * 2], data[i * 2 + 1]);
			}
		}
		return sendPost(command, nvpData);
	}
	
	/**
	 * Sendet einen PostRequest
	 * 
	 * @param command Das Kommando (z.B. login für http://www.thuermchen.com/api/login)
	 * @param data Die Daten, die per POST übegeben werden sollen
	 * @return Die Antwort als String
	 * @throws IOException 
	 */
	public String sendPost(String command, NameValuePair...data) throws IOException{
		HttpPost post = new HttpPost(command == null || command.length() == 0 ? url.substring(0, url.length() - 1) : url + command);
		if(data.length != 0) {
			post.setEntity(new UrlEncodedFormEntity(Arrays.asList(data)));
		}
		
		for(Cookie cookie : cookies.getCookies()) {
			System.out.println(cookie);
		}
		
		HttpResponse response = http.execute(post);
		
		String responseString = getString(response.getEntity().getContent());
		
		if(response.getStatusLine().getStatusCode() != 200) {
			ErrorLog.write("ERROR: Executing post request to " + url + command + " failed! ErrorCode: " + response.getStatusLine().getStatusCode() + ", ErrorMessage: " + responseString);
			System.out.println(command);
			System.out.println(responseString);
			Dialog.error(new JSONObject(responseString).getString("error"));
			return null;
		}
		
		return responseString;
	}
	
	public String sendGet(String command) throws IOException {
		return sendGet(command, new NameValuePair[0]);
	}
	
	public String sendGet(String command, String...data) throws IOException {
		if(data.length % 2 != 0) {
			throw new IllegalArgumentException("Pöse pöse, data muss immer eine Länge % 2 = 0 haben!");
		}
		NameValuePair[] nvpData = new BasicNameValuePair[data.length / 2];
		for(int i = 0; i < nvpData.length; i++) {
			nvpData[i] = new BasicNameValuePair(data[i * 2], data[i * 2 + 1]);
		}
		return sendGet(command, nvpData);
	}
	
	/**
	 * Sendet einen GetRequest
	 * 
	 * @param command Das Kommando (z.B. logout für http://www.thuermchen.com/api/logout)
	 * @param data Die Daten, die per GET übegeben werden sollen
	 * @return Die Antwort als String
	 * @throws IOException 
	 */
	public String sendGet(String command, NameValuePair...data) throws IOException {
		
		String args = "";
		for(NameValuePair pair : data) {
			args += args.isEmpty() ? "?" : "&";
			args += pair.getName() + "=" + pair.getValue();
		}
		
		HttpGet get = new HttpGet(command == null || command.isEmpty() ? url.substring(0, url.length() - 1) + args : url + command + args);
		
		HttpResponse response = http.execute(get);
		
		String responseString = getString(response.getEntity().getContent());
		
		if(response.getStatusLine().getStatusCode() != 200) {
			ErrorLog.write("ERROR: Executing get request to " + url + command + " failed! ErrorCode: " + response.getStatusLine().getStatusCode() + ", ErrorMessage: " + response.getStatusLine().getReasonPhrase());
			Dialog.error(new JSONObject(responseString).getString("error"));
			return null;
		}
		
		return responseString;
	}
	
	private String getString(InputStream in) throws IOException {
		StringBuilder responseContent = new StringBuilder();
		byte[] buffer = new byte[1024];
		int read;
		
		while((read = in.read(buffer)) > 0) {
			for(int i = 0; i < read; i++) {
				responseContent.append((char) buffer[i]);
			}
		}
		
		return responseContent.toString();
	}
	
	public Cookie createCookie(String key, String value) {
		BasicClientCookie cookie = new BasicClientCookie(key, value);
		cookie.setCreationDate(new Date(System.currentTimeMillis()));
		cookie.setSecure(false);
		cookie.setDomain(cookieUrl);
		cookie.setPath("/");
		return cookie;
	}
	
}

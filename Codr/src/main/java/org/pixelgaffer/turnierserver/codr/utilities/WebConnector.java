package org.pixelgaffer.turnierserver.codr.utilities;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
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
import net.lingala.zip4j.model.ZipParameters;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.CodrAi;
import org.pixelgaffer.turnierserver.codr.CodrGame;
import org.pixelgaffer.turnierserver.codr.Version;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.CompileException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.DeletedException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.NewException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.NothingDoneException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.UpdateException;



public class WebConnector {
	
	private final String url;
	private final String cookieUrl;
	
	private CookieStore cookies = new BasicCookieStore();
	private CloseableHttpClient http = HttpClients.custom().setDefaultCookieStore(cookies).build();
	
	
	/**
	 * Erstellt einen neuen Web Connector
	 * 
	 * @param url
	 *            Die URL der API (z.B. http://www.thuermchen.com/api/ <- Der
	 *            '/' muss da sein)
	 */
	public WebConnector(final String url, final String cookieUrl) {
		this.url = url;
		this.cookieUrl = cookieUrl;
		readFromFile();
	}
	
	
	/**
	 * Loggt den Benutzer ein
	 * 
	 * @param username
	 *            Der Benutzername
	 * @param password
	 *            Der Passwort
	 * @return Gibt an ob das Login erfolgreich war
	 * @throws IOException
	 */
	public boolean login(String username, String password) throws IOException {
		boolean result = sendPost("login", "email", username, "password", password, "remember", "true") != null;
		saveToFile();
		return result;
	}
	
	
	public String getUserName() {
		try {
			String json = toString(sendPost("loggedin"));
			if(json == null) {
				throw new IOException();
			}
			return new JSONObject(json).getString("username");
		} catch (IOException e) {
			ErrorLog.write("Abfrage des Nutzernamens nicht möglich");
			return null;
		}
	}
	
	public int getUserID() {
		try {
			String json = toString(sendPost("loggedin"));
			if(json == null) {
				throw new IOException();
			}
			return new JSONObject(json).getInt("id");
		} catch (IOException e) {
			ErrorLog.write("Abfrage der Nutzer ID nicht möglich");
			return -1;
		}
	}
	
	public int getLangID(String langName) {
		try {
			String json = toString(sendPost("langs"));
			if(json == null) {
				throw new IOException();
			}
			JSONArray array = new JSONArray(json);
			for(int i = 0; i < array.length(); i++) {
				if(array.getJSONObject(i).getString("name").equals(langName)) {
					return array.getJSONObject(i).getInt("id");
				}
			}
			ErrorLog.write("Es gibt keine Sprache mit dem Namen " + langName);
			return -1;
		} catch (IOException e) {
			ErrorLog.write("Abfrage der Sprachen nicht möglich");
			return -1;
		}
	}
	
	public int getGametypeID(String gametypeName) {
		try {
			String json = toString(sendPost("gametypes"));
			if(json == null) {
				throw new IOException();
			}
			JSONArray array = new JSONArray(json);
			for(int i = 0; i < array.length(); i++) {
				if(array.getJSONObject(i).getString("name").equals(gametypeName)) {
					return array.getJSONObject(i).getInt("id");
				}
			}
			ErrorLog.write("Es gibt keinen Spieltyp mit dem Namen " + gametypeName);
			return -1;
		} catch (IOException e) {
			ErrorLog.write("Abfrage der Spieltypen nicht möglich");
			return -1;
		}
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
			if (getSession() == null || getRememberToken() == null) {
				return false;
			}
			boolean result = sendPost("loggedin") != null;
			if (!result) {
				setTokens(null, null);
			}
			return result;
		} catch (IOException e) {
			ErrorLog.write("Fehler bei der Abfrage des Loginstatuses: " + e.getLocalizedMessage());
			return false;
		}
	}
	
	
	public ObservableList<CodrAi> getAis(String game) {
		ObservableList<CodrAi> result = FXCollections.observableArrayList();
		String json;
		try {
			json = toString(sendGet("ais/" + getGametypeID(game)));
		} catch (IOException e) {
			return result;
		}
		if (json == null) {
			return result;
		}
		JSONArray ais = new JSONArray(json);
		
		for (int i = 0; i < ais.length(); i++) {
			result.add(new CodrAi(ais.getJSONObject(i), this));
		}
		
		return result;
	}
	
	
	/**
	 * Gibt das Bild einer AI zurück
	 * 
	 * @param id
	 *            Die id der AI
	 * @return Das Bild der AI
	 * @throws IOException
	 */
	public Image getImage(int id) throws IOException {
		return new Image(new ByteArrayInputStream(sendGet("ai/" + id + "/icon")));
	}
	
	
	public List<CodrGame> getGames() {
		throw new UnsupportedOperationException("Ich bin so pöse!");
	}
	
	
	public void uploadVersion(Version version) throws ZipException, IOException {
		HttpPost post = new HttpPost("ai/id/new_version_from_zip");
		ZipFile zip = new ZipFile(Files.createTempFile(version.ai.title + "v" + version.number + System.currentTimeMillis(), ".zip").toFile());
		ZipParameters params = new ZipParameters();
		params.setIncludeRootFolder(false);
		zip.addFolder(new File(Paths.version(version)), params);
		post.setEntity(new ByteArrayEntity(FileUtils.readFileToByteArray(zip.getFile())));
		HttpResponse response = http.execute(post);
		if(getOutput(response.getEntity().getContent()) == null) {
			throw new IOException("Konnte nicht zum Server verbinden");
		}
	}
	
	
	public boolean createAi(CodrAi ai, String name) {
		try {
			sendGet("ai/create", "name", name, "desc", ai.description, "lang", getLangID(ai.language) + "", "type", getGametypeID(ai.gametype) + "");
			return true;
		} catch (IOException e) {
			ErrorLog.write("Konnte AI nicht erstellen: " + e.getLocalizedMessage());;
			return false;
		}
	}
	
	public String compile(Version version) throws IOException, CompileException {
		String json = toString(sendGet("ai/" + version.ai.id + "/compile_blocking"));
		if(json == null) {
			throw new IOException("Fehler bei der Verbindung mit dem Server");
		}
		JSONObject result = new JSONObject(json);
		if(result.getString("error") != null) {
			throw new CompileException(result.getString("compileoutput"), result.getString("error"));
		}
		return result.getString("compileoutput");
	}
	
	
	public ObservableList<CodrAi> getOwnAis(String game) {
		return FXCollections.observableArrayList(getAis(game).stream().filter((CodrAi ai) -> ai.userName == getUserName()).collect(Collectors.toList()));
	}
	
	
	/**
	 * Pingt den Server
	 * 
	 * @return Ob der Server erreichbar ist
	 */
	public boolean ping() {
		try {
			String result = toString(sendGet(null));
			return result != null && result.equals("PONG!");
		} catch (IOException e) {
			return false;
		}
	}
	
	
	public ObservableList<String> loadGametypesFromFile() {
		ObservableList<String> result = FXCollections.observableArrayList();
		
		try {
			for (String line : FileUtils.readLines(new File(Paths.gameTypesFile()))) {
				result.add(line.split("->")[0]);
			}
		} catch (IOException e) {
			ErrorLog.write("Konnte Spieltypen nicht aus Datei laden. Dies ist beim ersten Starten zu erwarten: " + e.getLocalizedMessage());
			return null;
		}
		
		return result;
	}
	
	
	public ObservableList<String> loadLangsFromFile() {
		ObservableList<String> result = FXCollections.observableArrayList();
		
		try {
			result.addAll(FileUtils.readLines(new File(Paths.langsFile())));
		} catch (IOException e) {
			return null;
		}
		return result;
	}
	
	
	public void updateLanguages() throws DeletedException, NewException, NothingDoneException, IOException {
		ObservableList<String> result = FXCollections.observableArrayList();
		
		String json = null;
		try {
			json = toString(sendGet("langs"));
		} catch (IOException e) {
			ErrorLog.write("Die Sprachen konnten nicht heruntergeladen werden: " + e.getLocalizedMessage());
		}
		if (json == null) {
			throw new IOException("Keine oder böse Antwort vom Server");
		}
		
		File langsFile = new File(Paths.langsFile());
		List<String> langsInFile = new ArrayList<String>();
		try {
			langsInFile = FileUtils.readLines(langsFile);
		} catch (IOException e) {
			ErrorLog.write("Konnte Sprachen nicht aus Datei lesen. Dies ist beim ersten Start zu erwarten: " + e.getLocalizedMessage());
		}
		
		boolean newLangs = false;
		boolean deleted = false;
		
		JSONArray langs = new JSONArray(json);
		for (int i = 0; i < langs.length(); i++) {
			String lang = langs.getJSONObject(i).getString("name");
			if (!langsInFile.contains(lang)) {
				newLangs = true;
			}
			result.add(lang);
		}
		
		langsFile.delete();
		try {
			langsFile.getParentFile().mkdirs();
			langsFile.createNewFile();
			for (int i = 0; i < result.size(); i++) {
				FileUtils.write(langsFile, result.get(i) + "\n", true);
			}
		} catch (IOException e) {
			ErrorLog.write("Die Sprachen konnten nicht in die Datei geschrieben werden!");
		}
		
		for (String lang : langsInFile) {
			if (!result.contains(lang)) {
				deleted = true;
			}
		}
		
		if (newLangs) {
			throw new NewException(result);
		}
		if (deleted) {
			throw new DeletedException(result);
		}
		throw new NothingDoneException();
		
	}
	
	
	public void updateGametypes() throws NewException, UpdateException, NothingDoneException, DeletedException, IOException {
		ObservableList<String> result = FXCollections.observableArrayList();
		
		String json = null;
		try {
			json = toString(sendGet("gametypes"));
		} catch (IOException e) {
			ErrorLog.write("Die Spieltypen konnten nicht heruntergeladen werden: " + e.getLocalizedMessage());
		}
		if (json == null) {
			throw new IOException("Keine oder böse Antwort vom Server");
		}
		
		JSONArray gametypes = new JSONArray(json);
		
		List<String> fileLines = new ArrayList<>();
		try {
			fileLines = FileUtils.readLines(new File(Paths.gameTypesFile()));
		} catch (IOException e) {
			ErrorLog.write("Die Spieltypen konnten nicht aus der Datei gelesen werden: " + e.getLocalizedMessage());
			ErrorLog.write("Es werden nun alle Spieltypen geladen!");
		}
		
		List<String> gametypesInFile = new ArrayList<>();
		List<String> gametypesFromFrontend = new ArrayList<>();
		for (String fileLine : fileLines) {
			gametypesInFile.add(fileLine.split("->")[0]);
		}
		
		boolean updated = false;
		boolean somethingNew = false;
		String[] lines = new String[gametypes.length()];
		
		for (int i = 0; i < gametypes.length(); i++) {
			JSONObject gametype = gametypes.getJSONObject(i);
			String apparentLine = gametype.getString("name") + "->" + gametype.getLong("last_modified");
			
			gametypesFromFrontend.add(gametype.getString("name"));
			
			if (!fileLines.contains(apparentLine)) {
				if (!loadGamelogic(gametype.getInt("id"), gametype.getString("name")) || !loadDataContainer(gametype.getInt("id"), gametype.getString("name"))) {
					ErrorLog.write("Konnte Spiel " + gametype.getString("name") + " nicht aktualisieren!");
					continue;
				} else {
					updated = true;
					if (!gametypesInFile.contains(gametype.getString("name"))) {
						somethingNew = true;
					}
				}
			}
			lines[gametype.getInt("id") - 1] = apparentLine;
		}
		
		// Speichern in der Datei
		try {
			File gametypesFile = new File(Paths.gameTypesFile());
			gametypesFile.delete();
			gametypesFile.getParentFile().mkdirs();
			gametypesFile.createNewFile();
			for (String line : lines) {
				if (line != null) {
					result.add(line.split("->")[0]);
					FileUtils.write(gametypesFile, line + System.lineSeparator(), true);
				}
			}
		} catch (IOException e) {
			ErrorLog.write("Die Spieltypen konnten nicht in die Datei geschrieben werden!");
		}
		
		boolean deleted = false;
		for (String gametype : gametypesInFile) {
			if (!gametypesFromFrontend.contains(gametype)) {
				deleted = true;
				try {
					FileUtils.deleteDirectory(new File(Paths.downloadGameType(gametype)));
				} catch (IOException e) {
					ErrorLog.write("Konnte Spieltyp " + gametype + " nicht löschen: " + e.getLocalizedMessage());
				}
				break;
			}
		}
		
		if (somethingNew) {
			throw new NewException(result);
		}
		if (deleted) {
			throw new DeletedException(result);
		}
		if (updated) {
			throw new UpdateException();
		}
		throw new NothingDoneException();
	}
	
	
	public boolean loadGamelogic(int game, String gameName) {
		byte[] logic;
		try {
			logic = sendGet("gamelogic/" + game);
		} catch (IOException e) {
			ErrorLog.write("Spiellogik konnte nicht heruntergeladen werden: " + e.getLocalizedMessage());
			return false;
		}
		
		if (logic == null) {
			return false;
		}
		
		try {
			FileUtils.writeByteArrayToFile(new File(Paths.gameLogic(gameName)), logic);
		} catch (IOException e) {
			ErrorLog.write("Spiellogik konnte nicht gespeichert werden: " + e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	
	public boolean loadDataContainer(int game, String gameName) {
		byte[] libraries;
		try {
			libraries = sendGet("data_container/" + game);
		} catch (IOException e) {
			ErrorLog.write("Der Data Container konnten nicht heruntergeladen werden: " + e.getLocalizedMessage());
			return false;
		}
		
		if (libraries == null) {
			return false;
		}
		
		try {
			File tempZip = File.createTempFile("datacontainer", System.currentTimeMillis() + ".zip");
			FileUtils.writeByteArrayToFile(tempZip, libraries);
			ZipFile zipFile = new ZipFile(tempZip);
			tempZip.deleteOnExit();
			File zip;
			zipFile.extractAll((zip = Files.createTempDirectory("datacontainerUnzipped" + System.currentTimeMillis()).toFile()).getAbsolutePath());
			zip.deleteOnExit();
			for (File file : new File(zip, "AiLibraries").listFiles()) {
				if (file.isFile()) {
					continue;
				}
				File target = new File(Paths.ailibrary(gameName, file.getName()));
				target.mkdirs();
				FileUtils.copyDirectory(file, target);
			}
			for (File file : new File(zip, "SimplePlayers").listFiles()) {
				if (file.isFile()) {
					continue;
				}
				File target = new File(Paths.simplePlayer(gameName, file.getName()) + "/src");
				FileUtils.deleteDirectory(new File(Paths.simplePlayer(gameName, file.getName())).getParentFile());
				target.mkdirs();
				File property = new File(Paths.simplePlayer(gameName, file.getName()) + "/..", "properties.txt");
				property.createNewFile();
				FileUtils.write(property, "versionAmount=1" + System.lineSeparator() + "gametype=" + gameName + System.lineSeparator() + "description=Das ist der " + file.getName() + "-SimplePlayer"
						+ System.lineSeparator() + "language=" + file.getName());
				property = new File(property.getParent() + "/v0/properties.txt");
				property.createNewFile();
				FileUtils.write(property, "uploaded=false" + System.lineSeparator() + "compileOutput=" + System.lineSeparator() + "qualifyOutput=" + System.lineSeparator() + "qualified=false"
						+ System.lineSeparator() + "compiled=false" + System.lineSeparator() + "finished=false" + System.lineSeparator() + "executeCommand=");
				FileUtils.copyDirectory(file, target);
			}
		} catch (IOException | ZipException e) {
			e.printStackTrace();
			ErrorLog.write("Ai Libraries konnte nicht entpackt werden: " + e.getLocalizedMessage());
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Setzt die Tokens einer Session
	 * 
	 * @param rememberToken
	 *            Den Remember Token
	 * @param sessionToken
	 *            Den Session Token
	 */
	public void setTokens(String rememberToken, String sessionToken) {
		if (rememberToken == null || sessionToken == null) {
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
			if (!file.exists()) {
				return;
			}
			String[] tokens = FileUtils.readFileToString(file).split("\n");
			if (tokens.length != 2) {
				return;
			}
			setTokens(tokens[1].isEmpty() ? null : tokens[1], tokens[0].isEmpty() ? null : tokens[0]);
		} catch (IOException e) {
			ErrorLog.write("ERROR SAVING SESSION: " + e.getMessage());
			return;
		}
	}
	
	
	public byte[] sendPost(String command) throws IOException {
		return sendPost(command, new NameValuePair[0]);
	}
	
	
	public byte[] sendPost(String command, String... data) throws IOException {
		if (data.length % 2 != 0) {
			throw new IllegalArgumentException("Pöse pöse, data muss immer eine Länge % 2 = 0 haben!");
		}
		NameValuePair[] nvpData = new BasicNameValuePair[data.length / 2];
		for (int i = 0; i < nvpData.length; i++) {
			if (data[i * 2] != null && data[i * 2 + 1] != null) {
				nvpData[i] = new BasicNameValuePair(data[i * 2], data[i * 2 + 1]);
			}
		}
		return sendPost(command, nvpData);
	}
	
	
	/**
	 * Sendet einen PostRequest
	 * 
	 * @param command
	 *            Das Kommando (z.B. login für
	 *            http://www.thuermchen.com/api/login)
	 * @param data
	 *            Die Daten, die per POST übegeben werden sollen
	 * @return Die Antwort als byte[]
	 * @throws IOException
	 */
	public byte[] sendPost(String command, NameValuePair... data) throws IOException {
		HttpPost post = new HttpPost(command == null || command.length() == 0 ? url.substring(0, url.length() - 1) : url + command);
		if (data.length != 0) {
			post.setEntity(new UrlEncodedFormEntity(Arrays.asList(data)));
		}
		
		HttpResponse response = http.execute(post);
		
		byte[] responseArray = getOutput(response.getEntity().getContent());
		
		if (response.getStatusLine().getStatusCode() != 200) {
			ErrorLog.write("ERROR: Executing post request to " + url + command + " failed! ErrorCode: " + response.getStatusLine().getStatusCode() + ", ErrorMessage: " + toString(responseArray));
			return null;
		}
		
		return responseArray;
	}
	
	
	public byte[] sendGet(String command) throws IOException {
		return sendGet(command, new NameValuePair[0]);
	}
	
	
	public byte[] sendGet(String command, String... data) throws IOException {
		if (data.length % 2 != 0) {
			throw new IllegalArgumentException("Pöse pöse, data muss immer eine Länge % 2 = 0 haben!");
		}
		NameValuePair[] nvpData = new BasicNameValuePair[data.length / 2];
		for (int i = 0; i < nvpData.length; i++) {
			nvpData[i] = new BasicNameValuePair(data[i * 2], data[i * 2 + 1]);
		}
		return sendGet(command, nvpData);
	}
	
	
	/**
	 * Sendet einen GetRequest
	 * 
	 * @param command
	 *            Das Kommando (z.B. logout für
	 *            http://www.thuermchen.com/api/logout)
	 * @param data
	 *            Die Daten, die per GET übegeben werden sollen
	 * @return Die Antwort als byte[]
	 * @throws IOException
	 */
	public byte[] sendGet(String command, NameValuePair... data) throws IOException {
		
		String args = "";
		for (NameValuePair pair : data) {
			args += args.isEmpty() ? "?" : "&";
			args += pair.getName() + "=" + pair.getValue();
		}
		
		HttpGet get = new HttpGet(command == null || command.isEmpty() ? url.substring(0, url.length() - 1) + args : url + command + args);
		
		HttpResponse response = http.execute(get);
		
		byte[] responseArray = getOutput(response.getEntity().getContent());
		
		if (response.getStatusLine().getStatusCode() != 200) {
			ErrorLog.write("ERROR: Executing get request to " + url + command + " failed! ErrorCode: " + response.getStatusLine().getStatusCode() + ", ErrorMessage: " + toString(responseArray));
			return null;
		}
		
		return responseArray;
	}
	
	
	private String toString(byte[] bytes) throws IOException {
		if (bytes == null) {
			throw new IOException();
		}
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	
	private byte[] getOutput(InputStream in) throws IOException {
		ByteArrayOutputStream responseContent = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int read;
		
		while ((read = in.read(buffer)) > 0) {
			for (int i = 0; i < read; i++) {
				responseContent.write(buffer[i]);
			}
		}
		
		return responseContent.toByteArray();
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

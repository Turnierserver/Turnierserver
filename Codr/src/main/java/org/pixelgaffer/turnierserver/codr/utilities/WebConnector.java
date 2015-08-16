package org.pixelgaffer.turnierserver.codr.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.codr.AiBase;
import org.pixelgaffer.turnierserver.codr.AiOnline;
import org.pixelgaffer.turnierserver.codr.AiSimple;
import org.pixelgaffer.turnierserver.codr.GameOnline;
import org.pixelgaffer.turnierserver.codr.Version;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.CompileException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.DeletedException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.NewException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.NothingDoneException;
import org.pixelgaffer.turnierserver.codr.utilities.Exceptions.UpdateException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

/**
 * koordiniert die Verbindung zum Server und führt Up-/Downloads aus
 *
 * @author Nico TODO: Nico, kommentier das mal alles!!!
 */
public class WebConnector {

	public String userName = null;
	public boolean isLoggedIn = false;
	public boolean isConnected = false;

	private final String url;

	private DateFormat cookieDateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.UK);

	private CookieStore cookies = new BasicCookieStore();
	private CloseableHttpClient http = HttpClients.custom().setDefaultCookieStore(cookies).build();


	/**
	 * Erstellt einen neuen Web Connector
	 * 
	 * @param url
	 *            Die URL der API (z.B. http://www.thuermchen.com/api/ <- Der
	 *            '/' muss da sein)
	 */
	public WebConnector(final String url) {
		this.url = url;
		readFromFile();
	}


	/**
	 * Lädt die angegebene Bibliothek vom Frontend herunter. <code>name</code>
	 * enthält name/version.
	 */
	public boolean getLibrary(String language, String name) {
		try {
			downloadAndExtractZip(new File(Paths.library(language, name)), "lib/" + language + "/" + name);
		} catch (IOException | ZipException e) {
			ErrorLog.write("Konnte Bibliothek " + name + " in der Sprache " + language + " nicht herunterladen: " + e.getLocalizedMessage());
			return false;
		}
		return true;
	}


	/**
	 * Loggt den Benutzer ein
	 * 
	 * @param userName
	 *            Der Benutzername
	 * @param password
	 *            Der Passwort
	 * @return Gibt an ob das Login erfolgreich war
	 * @throws IOException
	 */
	public boolean login(String userName, String password) throws IOException {
		this.userName = userName;
		boolean result = sendPost("login", "email", userName, "password", password, "remember", "true") != null;
		saveToFile();
		if (result == false) {
			isLoggedIn = false;
			this.userName = null;
		}
		getUserName();
		return result;
	}


	public String getUserName() {
		try {
			String json = toString(sendPost("loggedin"));
			if (json == null) {
				throw new IOException();
			}
			userName = new JSONObject(json).getString("name");
			isLoggedIn = true;
			return userName;
		} catch (IOException e) {
			ErrorLog.write("Abfrage des Nutzernamens nicht möglich");
			isLoggedIn = false;
			userName = null;
			return null;
		}
	}


	public int getUserID() {
		try {
			String json = toString(sendPost("loggedin"));
			if (json == null) {
				throw new IOException();
			}
			return new JSONObject(json).getInt("id");
		} catch (IOException e) {
			ErrorLog.write("Abfrage der Nutzer ID nicht möglich");
			return -1;
		}
	}


	private int getLangID(String langName) {
		try {
			String json = toString(sendGet("langs"));
			if (json == null) {
				throw new IOException();
			}
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				if (array.getJSONObject(i).getString("name").equals(langName)) {
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


	private int getGametypeID(String gametypeName) {
		try {
			String json = toString(sendGet("gametypes"));
			if (json == null) {
				throw new IOException();
			}
			JSONArray array = new JSONArray(json);
			for (int i = 0; i < array.length(); i++) {
				if (array.getJSONObject(i).getString("name").equals(gametypeName)) {
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
		userName = null;
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
				cookies.getCookies().clear();
			}
			return result;
		} catch (IOException e) {
			ErrorLog.write("Fehler bei der Abfrage des Loginstatuses: " + e);
			return false;
		}
	}


	public ObservableList<AiOnline> getAis(String game) {
		ObservableList<AiOnline> result = FXCollections.observableArrayList();
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
			result.add(new AiOnline(ais.getJSONObject(i), this));
		}

		return result;
	}


	public ObservableList<GameOnline> getGames(String game) {
		ObservableList<GameOnline> result = FXCollections.observableArrayList();
		String json;
		try {
			json = toString(sendGet("games/" + getGametypeID(game)));
		} catch (IOException e) {
			return result;
		}
		if (json == null) {
			return result;
		}
		JSONArray games = new JSONArray(json);
		for (int i = 0; i < games.length(); i++) {
			result.add(new GameOnline(games.getJSONObject(i), this));
		}

		return result;
	}


	public ObservableList<Integer> getGameIDs(int ai) {
		ObservableList<Integer> result = FXCollections.observableArrayList();
		String json;
		try {
			json = toString(sendGet("ai/" + ai + "/games"));
		} catch (IOException e) {
			return result;
		}
		if (json == null) {
			return result;
		}
		JSONArray ais = new JSONArray(json);

		for (int i = 0; i < ais.length(); i++) {
			result.add(ais.getJSONObject(i).getInt("id"));
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
		try {
			return new Image(new ByteArrayInputStream(sendGet("ai/" + id + "/icon")));
		} catch (NullPointerException e) {
			ErrorLog.write("Konnte das Bild der KI " + id + " nicht empfangen.");
			return null;
		}
	}


	public void changeImage(File img, int id) throws IOException {
		if (img == null) {
			return;
		}
		HttpPost post = new HttpPost("ai/" + id + "/upload_icon");
		ByteArrayEntity entity = new ByteArrayEntity(FileUtils.readFileToByteArray(img));
		post.setEntity(entity);
		HttpResponse response = http.execute(post);
		if (getOutput(response.getEntity().getContent()) == null) {
			throw new IOException("Konnte nicht zum Server verbinden");
		}
	}


	public void changeDescription(String description, int id) {
		String result;
		try {
			result = toString(sendGet("ai/" + id + "/update", "description", description));
			if (result == null) {
				throw new IOException("A problem occured while trying to update the description!");
			}
		} catch (IOException e) {
			ErrorLog.write("Die Beschreibung der KI konnte nicht geändert werden.");
			e.printStackTrace();
		}
	}


	public void deleteKI(int id) {
		String result;
		try {
			result = toString(sendGet("ai/" + id + "/delete"));
			if (result == null) {
				throw new IOException("A problem occured while trying to delete ai!");
			}
		} catch (IOException e) {
			ErrorLog.write("Die AI konnte nicht gelöscht werden.");
			e.printStackTrace();
		}
	}


	public void uploadVersion(Version version, int id) throws ZipException, IOException {
		HttpPost post = new HttpPost(url + "ai/" + id + "/new_version_from_zip");
		
		File file = new File(System.getProperty("java.io.tmpdir"), version.ai.title + "v" + version.number + System.currentTimeMillis() + ".zip");
		
		ZipFile zip = new ZipFile(file);
		ZipParameters params = new ZipParameters();
		params.setIncludeRootFolder(false);
		zip.createZipFileFromFolder(new File(Paths.version(version)), params, false, -1);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("file", FileUtils.readFileToByteArray(file));
		builder.setContentType(ContentType.DEFAULT_BINARY);
		post.setEntity(builder.build());
				
		file.deleteOnExit();
		
		HttpResponse response = http.execute(post);
		if (getOutput(response.getEntity().getContent()) == null) {
			throw new IOException("Konnte nicht zum Server verbinden");
		}
		
		changeImage(((AiSimple) version.ai).getPictureFile(), id);
	}


	public int createAi(AiBase ai, String name) {
		try {
			byte response[] = sendGet("ai/create", "name", name, "desc", ai.description, "lang", getLangID(ai.language) + "", "type", getGametypeID(ai.gametype) + "");
			if (response == null)
				throw new IOException();
			JSONObject json = new JSONObject(new String(response, StandardCharsets.UTF_8));
			json = json.getJSONObject("ai");
			return json.getInt("id");
		} catch (IOException e) {
			ErrorLog.write("Konnte AI nicht erstellen: " + e);
			;
			return -1;
		}
	}


	public String compile(int id) throws IOException, CompileException {
		String json = toString(sendGet("ai/" + id + "/compile_blocking"));
		if (json == null) {
			throw new IOException("Fehler bei der Verbindung mit dem Server");
		}
		JSONObject result = new JSONObject(json);
		if (!result.isNull("error")) {
			throw new CompileException(result.getString("compilelog"));
		}
		return result.getString("compilelog");
	}


	public ObservableList<AiOnline> getOwnAis() {
		return getUserAis(getUserID());
	}


	public ObservableList<AiOnline> getUserAis(int user) {
		ObservableList<AiOnline> result = FXCollections.observableArrayList();
		String json;
		try {
			json = toString(sendGet("user/" + user));
		} catch (IOException e) {
			return result;
		}
		if (json == null) {
			return result;
		}
		JSONArray ais = new JSONObject(json).getJSONArray("ais");

		for (int i = 0; i < ais.length(); i++) {
			result.add(new AiOnline(ais.getJSONObject(i), this));
		}

		return result;
	}


	public ObservableList<AiOnline> getOwnAis(String gametype) {
		return getUserAis(getUserID(), gametype);
	}


	public ObservableList<AiOnline> getUserAis(int user, String gametype) {
		ObservableList<AiOnline> ais = FXCollections.observableArrayList();
		ais.addAll(getUserAis(user).stream().filter(ai -> ai.gametype.equals(gametype)).collect(Collectors.toList()));
		return ais;
	}


	/**
	 * Pingt den Server
	 * 
	 * @return Ob der Server erreichbar ist
	 */
	public boolean ping() {
		try {
			String result = toString(sendGet("ping"));
			return result != null;
		} catch (IOException e) {
			e.printStackTrace();
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
			ErrorLog.write("Konnte Spieltypen nicht aus Datei laden. Dies ist beim ersten Starten zu erwarten: " + e);
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
			ErrorLog.write("Die Sprachen konnten nicht heruntergeladen werden: " + e);
		}
		if (json == null) {
			throw new IOException("Keine oder böse Antwort vom Server");
		}

		File langsFile = new File(Paths.langsFile());
		List<String> langsInFile = new ArrayList<String>();
		try {
			langsInFile = FileUtils.readLines(langsFile);
		} catch (IOException e) {
			ErrorLog.write("Konnte Sprachen nicht aus Datei lesen. Dies ist beim ersten Start zu erwarten: " + e);
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
			ErrorLog.write("Die Spieltypen konnten nicht heruntergeladen werden: " + e);
		}
		if (json == null) {
			throw new IOException("Keine oder böse Antwort vom Server");
		}

		JSONArray gametypes = new JSONArray(json);

		List<String> fileLines = new ArrayList<>();
		try {
			fileLines = FileUtils.readLines(new File(Paths.gameTypesFile()));
		} catch (IOException e) {
			ErrorLog.write("Die Spieltypen konnten nicht aus der Datei gelesen werden: " + e);
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
					ErrorLog.write("Konnte Spieltyp " + gametype + " nicht löschen: " + e);
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


	public boolean updateCodr() throws IOException {
		return loadCodr();
	}


	private boolean loadCodr() {
		byte[] codr;
		try {
			codr = sendGet("download_codr");
		} catch (IOException e) {
			ErrorLog.write("Die neue Version von Codr konnte nicht heruntergeladen werden: " + e);
			return false;
		}

		if (codr == null)
			return false;

		try {
			File file = new File(Paths.newCodrVersion());
			file.mkdirs();
			file.delete();
			file.createNewFile();
			FileUtils.writeByteArrayToFile(new File(Paths.newCodrVersion()), codr);
		} catch (IOException e) {
			ErrorLog.write("Die neue Version von Codr konnte nicht gespeichert werden: " + e);
			return false;
		}
		return true;
	}


	private boolean loadGamelogic(int game, String gameName) {
		byte[] logic;
		try {
			logic = sendGet("gamelogic/" + game);
		} catch (IOException e) {
			ErrorLog.write("Spiellogik konnte nicht heruntergeladen werden: " + e);
			return false;
		}

		if (logic == null)
			return false;

		try {
			FileUtils.writeByteArrayToFile(new File(Paths.gameLogic(gameName)), logic);
		} catch (IOException e) {
			ErrorLog.write("Spiellogik konnte nicht gespeichert werden: " + e);
			return false;
		}
		return true;
	}


	private boolean loadDataContainer(int game, String gameName) {
		byte[] libraries;
		try {
			libraries = sendGet("data_container/" + game);
		} catch (IOException e) {
			ErrorLog.write("Der Data Container konnten nicht heruntergeladen werden: " + e);
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
				File property = new File(Paths.simplePlayer(gameName, file.getName()) + "/..", "aiProperties.txt");
				property.createNewFile();
				FileUtils.write(property, "versionAmount=1" + System.lineSeparator() + "gametype=" + gameName + System.lineSeparator() + "description=Das ist der " + file.getName() + "-SimplePlayer"
						+ System.lineSeparator() + "language=" + file.getName());
				property = new File(property.getParent() + "/v0/versionProperties.txt");
				property.createNewFile();
				FileUtils.write(property, "uploaded=false" + System.lineSeparator() + "compileOutput=" + System.lineSeparator() + "qualifyOutput=" + System.lineSeparator() + "qualified=false"
						+ System.lineSeparator() + "compiled=false" + System.lineSeparator() + "finished=false" + System.lineSeparator() + "executeCommand=");
				FileUtils.copyDirectory(file, target);
			}
		} catch (IOException | ZipException e) {
			e.printStackTrace();
			ErrorLog.write("Ai Libraries konnte nicht entpackt werden: " + e);
			return false;
		}

		return true;
	}


	/**
	 * Gibt den Session Token zurück
	 * 
	 * @return Den Session Token der Session
	 */
	private String getSession() {
		List<Cookie> cookie = cookies.getCookies().stream().filter((Cookie o) -> o.getName().equals("session")).collect(Collectors.toList());
		return cookie.isEmpty() ? null : cookie.get(0).getValue();
	}


	/**
	 * Gibt den Remember Token zurück
	 * 
	 * @return Den Remember Token der Session
	 */
	private String getRememberToken() {
		List<Cookie> cookie = cookies.getCookies().stream().filter((Cookie o) -> o.getName().equals("remember_token")).collect(Collectors.toList());
		return cookie.isEmpty() ? null : cookie.get(0).getValue();
	}


	/**
	 * Speichert die Session in eine Datei
	 */
	private void saveToFile() {
		File file = new File(Paths.sessionFile());
		try {
			Properties p = new Properties();
			List<Cookie> cookies = this.cookies.getCookies();

			p.setProperty("session.cookies", Integer.toString(cookies.size()));
			for (int i = 0; i < cookies.size(); i++) {
				Cookie c = cookies.get(i);
				p.setProperty("session.cookies." + i + ".domain", c.getDomain());
				if (c.getExpiryDate() != null)
					p.setProperty("session.cookies." + i + ".expiry", cookieDateFormat.format(c.getExpiryDate()));
				p.setProperty("session.cookies." + i + ".name", c.getName());
				p.setProperty("session.cookies." + i + ".path", c.getPath());
				p.setProperty("session.cookies." + i + ".value", c.getValue());
				p.setProperty("session.cookies." + i + ".version", Integer.toString(c.getVersion()));
			}

			p.store(new FileOutputStream(file), "Die Session von Codr");
		} catch (IOException e) {
			ErrorLog.write("Fehler beim Speichern der Session: " + e.getMessage());
			return;
		}
	}


	/**
	 * Holt die Session aus einer Datei
	 */
	private void readFromFile() {
		File file = new File(Paths.sessionFile());
		try {
			if (!file.exists()) {
				return;
			}

			Properties p = new Properties();
			p.load(new FileInputStream(file));

			int cookies = Integer.parseInt(p.getProperty("session.cookies"));
			for (int i = 0; i < cookies; i++) {
				String name = p.getProperty("session.cookies." + i + ".name");
				String value = p.getProperty("session.cookies." + i + ".value");
				BasicClientCookie c = new BasicClientCookie(name, value);
				c.setDomain(p.getProperty("session.cookies." + i + ".domain"));
				if (p.containsKey("session.cookies." + i + ".expiry"))
					c.setExpiryDate(cookieDateFormat.parse(p.getProperty("session.cookies." + i + ".expiry")));
				else
					c.setExpiryDate(null);
				c.setPath(p.getProperty("session.cookies." + i + ".path"));
				c.setVersion(Integer.parseInt(p.getProperty("session.cookies." + i + ".version")));

				this.cookies.addCookie(c);
			}

		} catch (IOException | ParseException e) {
			ErrorLog.write("Fehler beim Laden der Session: " + e.getMessage());
			return;
		}
	}


	private File downloadAndExtractZip(File folder, String command) throws IOException, ZipException {
		File tempZip = File.createTempFile("codr", System.currentTimeMillis() + ".zip");
		downloadFile(tempZip, command);
		ZipFile zipFile = new ZipFile(tempZip);
		tempZip.deleteOnExit();
		File zip;
		zipFile.extractAll((zip = Files.createTempDirectory("codrUnzipped" + System.currentTimeMillis()).toFile()).getAbsolutePath());
		zip.deleteOnExit();
		for (File file : zip.listFiles()) {
			if (file.isFile()) {
				FileUtils.copyFile(file, new File(folder, file.getName()));
			} else {
				new File(folder, file.getName()).mkdirs();
				FileUtils.copyDirectory(file, new File(folder, file.getName()));
			}
		}
		return folder;
	}


	private File downloadFile(File file, String command) throws IOException {
		byte[] data;
		data = sendGet(command);
		if (data == null) {
			throw new IOException("Couldn't download from " + command + " to " + file);
		}
		FileUtils.writeByteArrayToFile(file, data);
		return file;
	}


	private byte[] sendPost(String command) throws IOException {
		return sendPost(command, new NameValuePair[0]);
	}


	private byte[] sendPost(String command, String... data) throws IOException {
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
	private byte[] sendPost(String command, NameValuePair... data) throws IOException {
		try {
			HttpPost post = new HttpPost(command == null || command.length() == 0 ? url.substring(0, url.length() - 1) : url + command);
			if (data.length != 0) {
				post.setEntity(new UrlEncodedFormEntity(Arrays.asList(data)));
			}

			HttpResponse response = http.execute(post);

			byte[] responseArray = getOutput(response.getEntity().getContent());

			if (response.getStatusLine().getStatusCode() != 200) {
				ErrorLog.write("ERROR: Executing post request to " + url + command + " failed! ErrorCode: " + response.getStatusLine().getStatusCode() + ", ErrorMessage: " + toString(responseArray));
				isConnected = false;
				return null;
			}

			isConnected = true;
			return responseArray;
		} catch (IOException e) {
			isConnected = false;
			throw e;
		}
	}


	private byte[] sendGet(String command) throws IOException {
		return sendGet(command, new NameValuePair[0]);
	}


	private byte[] sendGet(String command, String... data) throws IOException {
		if (data.length % 2 != 0) {
			throw new IllegalArgumentException("Data muss immer eine Länge % 2 = 0 haben!");
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
	private byte[] sendGet(String command, NameValuePair... data) throws IOException {
		try {
			String args = "";
			for (NameValuePair pair : data) {
				args += args.isEmpty() ? "?" : "&";
				args += URLEncoder.encode(pair.getName(), "UTF8") + "=" + URLEncoder.encode(pair.getValue(), "UTF8");
			}

			HttpGet get = new HttpGet(command == null || command.isEmpty() ? url.substring(0, url.length() - 1) + args : url + command + args);

			HttpResponse response = http.execute(get);

			byte[] responseArray = getOutput(response.getEntity().getContent());

			if (response.getStatusLine().getStatusCode() != 200) {
				ErrorLog.write("ERROR: Executing get request to " + url + command + " failed! ErrorCode: " + response.getStatusLine().getStatusCode());// +
				// ", ErrorMessage: "
				// +
				// toString(responseArray));
				isConnected = false;
				return null;
			}

			isConnected = true;
			return responseArray;
		} catch (IOException e) {
			isConnected = false;
			throw e;
		}
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
}

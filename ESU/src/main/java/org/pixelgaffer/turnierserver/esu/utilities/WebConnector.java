package org.pixelgaffer.turnierserver.esu.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import org.json.JSONObject;

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
		try {
			System.out.println(isLoggedIn());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public boolean isLoggedIn() throws IOException {
		if(getSession() == null || getRememberToken() == null) {
			return false;
		}
		boolean result = sendPost("loggedin") != null;
		if(!result) {
			setTokens(null, null);
		}
		return result;
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
		HttpPost post = new HttpPost(url + command);
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
			Dialog.error(new JSONObject(responseString).getString("error"));
			return null;
		}
		
		return responseString;
	}
	
	public String sendGet(String command) throws IOException {
		return sendPost(command, new NameValuePair[0]);
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
		
		HttpGet get = new HttpGet(url + command + args);
		
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

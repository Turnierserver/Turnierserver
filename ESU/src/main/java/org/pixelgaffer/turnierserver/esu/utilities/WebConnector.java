package org.pixelgaffer.turnierserver.esu.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ErrorManager;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

public class WebConnector {
	
	private final String url;
	
	private CookieStore cookies = new BasicCookieStore();
	private CloseableHttpClient http = HttpClients.custom().setDefaultCookieStore(cookies).build();
	
	/**
	 * Erstellt einen neuen Web Connector
	 * 
	 * @param url Die URL der API (z.B. http://www.thuermchen.com/api/ <- Der '/' muss da sein)
	 */
	public WebConnector(final String url){
		this.url = url;
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
		boolean result = sendPost("login", new BasicNameValuePair("email", username), new BasicNameValuePair("password", password)) != null;
		saveToFile();
		return result;
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
		cookies.addCookie(new BasicClientCookie("remember_token", rememberToken));
		cookies.addCookie(new BasicClientCookie("session", sessionToken));
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
		
		HttpResponse response = http.execute(post);
		
		if(response.getStatusLine().getStatusCode() != 200) {
			ErrorLog.write("ERROR: Executing post request to " + url + command + " failed! ErrorCode: " + response.getStatusLine().getStatusCode() + ", ErrorMessage: " + getString(response.getEntity().getContent()));
			return null;
		}
		
		return getString(response.getEntity().getContent());
	}
	
	public String sendGet(String command, NameValuePair...data) throws IOException {
		
		String args = "";
		for(NameValuePair pair : data) {
			args += args.isEmpty() ? "?" : "&";
			args += pair.getName() + "=" + pair.getValue();
		}
		
		HttpGet get = new HttpGet(url + command + args);
		
		HttpResponse response = http.execute(get);
		
		if(response.getStatusLine().getStatusCode() != 200) {
			ErrorLog.write("ERROR: Executing get request to " + url + command + " failed! ErrorCode: " + response.getStatusLine().getStatusCode() + ", ErrorMessage: " + response.getStatusLine().getReasonPhrase());
			return null;
		}
		
		return getString(response.getEntity().getContent());
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
	
	
}

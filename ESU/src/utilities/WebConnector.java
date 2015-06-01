package utilities;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class WebConnector {
	
	private final String ip;
	
	private CloseableHttpClient http = HttpClients.createDefault();
	
	public WebConnector(final String ip){
		this.ip = ip;
	}
	
	public boolean login(String username, String password){
		return true;
	}
	
	public boolean sendPost(String command, NameValuePair[] data){
		return true;
	}
	
	
	
}

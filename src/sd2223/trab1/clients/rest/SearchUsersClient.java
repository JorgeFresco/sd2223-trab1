package sd2223.trab1.clients.rest;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;


public class SearchUsersClient {

	private static Logger Log = Logger.getLogger(SearchUsersClient.class.getName());
	
	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static void main(String[] args) throws IOException {
		
		if (args.length != 2) {
			System.err.println("Use: java sd2223.trab1.clients.rest.SearchUsersClient url pattern");
			return;
		}

		String serverUrl = args[0];
		String pattern = args[1];


		Log.info("Sending request to server.");

		var result = new RestUsersClient(URI.create(serverUrl)).searchUsers(pattern);
		System.out.println("Result: " + result);

	}

}

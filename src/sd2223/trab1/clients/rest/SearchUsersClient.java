package sd2223.trab1.clients.rest;

import sd2223.trab1.clients.rest.RestUsersClient;

import java.io.IOException;
import java.net.URI;


public class SearchUsersClient {
	
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


		System.out.println("Sending request to server.");

		var result = new RestUsersClient(URI.create(serverUrl)).searchUsers(pattern);
		System.out.println("Result: " + result);

	}

}

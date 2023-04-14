package sd2223.trab1.clients.soap;

import java.net.URI;
import java.util.logging.Logger;
import sd2223.trab1.api.User;

public class CreateUserClient {

	private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

	public static void main(String[] args) {
		if( args.length != 5) {
			System.err.println("usage: serverUri name pwd domain displayName");
			return;
		}
		
		var serverURI = args[0];
		var name = args[1];
		var pwd = args[2];
		var domain = args[3];
		var displayName = args[4];
		
		var users = new SoapUsersClient( URI.create( serverURI ));
		
		var res = users.createUser( new User( name, pwd, domain, displayName) );

		Log.info("Sending request to server.");

		System.out.println("Result: " + res );
	}

}

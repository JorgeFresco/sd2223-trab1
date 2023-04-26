package sd2223.trab1.clients.soap;

import java.net.URI;
import java.util.logging.Logger;

public class SearchUsersClient {

    private static Logger Log = Logger.getLogger(SearchUsersClient.class.getName());
    public static void main(String[] args) {
        if( args.length != 2) {
            System.err.println( "usage: serverUri pattern");
            System.exit(0);

        }

        String serverURI = args[0];
        String pattern = args[1];

        var users = new SoapUsersClient( URI.create( serverURI ));

        Log.info("Sending request to server.");

        var res = users.searchUsers(pattern);
        System.out.println("Result: " + res );
    }
}

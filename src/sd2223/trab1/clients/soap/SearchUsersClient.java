package sd2223.trab1.clients.soap;

import sd2223.trab1.api.User;

import java.net.URI;

public class SearchUsersClient {
    public static void main(String[] args) {
        if( args.length != 2) {
            System.err.println( "usage: serverUri pattern");
            System.exit(0);

        }

        String serverURI = args[0];
        String pattern = args[1];

        var users = new SoapUsersClient( URI.create( serverURI ));

        var res = users.searchUsers(pattern);
        System.out.println( res );
    }
}

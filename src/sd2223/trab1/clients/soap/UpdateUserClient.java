package sd2223.trab1.clients.soap;

import sd2223.trab1.api.User;

import java.net.URI;

public class UpdateUserClient {
    public static void main(String[] args) {
        if( args.length != 6) {
            System.err.println( "usage: serverUri name oldpwd pwd domain displayName");
            System.exit(0);

        }

        String serverURI = args[0];
        String name = args[1];
        String oldpwd = args[2];
        String pwd = args[3];
        String domain = args[4];
        String displayName = args[5];


        var users = new SoapUsersClient( URI.create( serverURI ));

        var res = users.updateUser(name, oldpwd, new User( name, pwd, domain, displayName));
        System.out.println( res );
}
}

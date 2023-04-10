package sd2223.trab1.clients.soap;

import sd2223.trab1.api.User;
import sd2223.trab1.clients.rest.RestUsersClient;

import java.net.URI;

public class DeleteUserClient {

    public static void main(String[] args) {
        if( args.length != 3) {
        System.err.println( "usage: serverUri name pwd");
        System.exit(0);

        }

    String serverURI = args[0];
    String name = args[1];
    String pwd = args[2];


    var users = new SoapUsersClient( URI.create( serverURI ));

    var res = users.deleteUser(name, pwd);
    System.out.println( res );
}}

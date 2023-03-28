package trab1.clients;

import java.io.IOException;
import java.net.URI;

public class DeleteUserClient {

    public static void main(String[] args) throws IOException {

        if( args.length != 3) {
            System.err.println( "Use: java trab1.clients.DeleteUserClient url name pwd");
            return;
        }

        String serverUrl = args[0];
        String name = args[1];
        String pwd = args[2];

        System.out.println("Sending request to server.");

        var result = new RestUsersClient(URI.create(serverUrl)).deleteUser(name, pwd);
        System.out.println("Result: " + result);

    }
}

package sd2223.trab1.clients.rest;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class DeleteUserClient {

    private static Logger Log = Logger.getLogger(DeleteUserClient.class.getName());

    public static void main(String[] args) throws IOException {

        if( args.length != 3) {
            System.err.println( "Use: java sd2223.trab1.clients.rest.DeleteUserClient url name pwd");
            return;
        }

        String serverUrl = args[0];
        String name = args[1];
        String pwd = args[2];

        Log.info("Sending request to server.");

        var result = new RestUsersClient(URI.create(serverUrl)).deleteUser(name, pwd);
        System.out.println("Result: " + result);

    }
}
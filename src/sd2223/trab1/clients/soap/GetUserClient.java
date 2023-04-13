package sd2223.trab1.clients.soap;

import sd2223.trab1.api.User;
import sd2223.trab1.clients.rest.CreateUserClient;
import sd2223.trab1.clients.rest.RestUsersClient;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class GetUserClient {

    private static Logger Log = Logger.getLogger(CreateUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("usage: serverUri name pwd");
            return;
        }

        String serverURI = args[0];
        String name = args[1];
        String pwd = args[2];

        Log.info("Sending request to server.");

        var users = new SoapUsersClient( URI.create( serverURI ));

        var res = users.getUser(name,pwd);
        System.out.println( res );
    }
}

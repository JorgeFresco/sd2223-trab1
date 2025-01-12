package sd2223.trab1.clients.soap;

import java.net.URI;
import java.util.logging.Logger;

public class GetUserClient {

    private static Logger Log = Logger.getLogger(GetUserClient.class.getName());

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("usage: serverUri name pwd");
            return;
        }

        String serverURI = args[0];
        String name = args[1];
        String pwd = args[2];

        var users = new SoapUsersClient( URI.create( serverURI ));

        Log.info("Sending request to server.");

        var res = users.getUser(name,pwd);
        System.out.println("Result: " + res );
    }
}

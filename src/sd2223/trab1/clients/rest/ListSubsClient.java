package sd2223.trab1.clients.rest;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class ListSubsClient {
    private static Logger Log = Logger.getLogger(PostMessageClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("Use: java trab1.clients.SubUserClient url user domain");
            return;
        }

        String serverUrl = args[0];
        String user = args[1];
        String domain = args[2];

        Log.info("Sending request to server.");

        var result = new RestFeedsClient(URI.create(serverUrl)).listSubs(user+"@"+domain);
        System.out.println("Result: " + result);
    }
}

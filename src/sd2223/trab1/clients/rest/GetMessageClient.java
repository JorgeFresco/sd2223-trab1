package sd2223.trab1.clients.rest;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class GetMessageClient {
    private static Logger Log = Logger.getLogger(GetMessageClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println("Use: java sd2223.trab1.clients.rest.GetMessageClient url user domain mid");
            return;
        }

        String serverUrl = args[0];
        String user = args[1];
        String domain = args[2];
        String mid = args[3];

        Log.info("Sending request to server.");

        var result = new RestFeedsClient(URI.create(serverUrl)).getMessage(user+"@"+domain,Long.parseLong(mid));
        System.out.println("Result: " + result);
    }
}

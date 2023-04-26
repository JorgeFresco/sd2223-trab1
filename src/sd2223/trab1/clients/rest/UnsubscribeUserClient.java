package sd2223.trab1.clients.rest;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class UnsubscribeUserClient {

    private static Logger Log = Logger.getLogger(UnsubscribeUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 6) {
            System.err.println("Use: java sd2223.trab1.clients.rest.UnsubscribeUserClient url user userDomain subUser subUserDomain pwd");
            return;
        }

        String serverUrl = args[0];
        String user = args[1];
        String userDomain = args[2];
        String subUser = args[3];
        String subUserDomain = args[4];
        String pwd = args[5];

        Log.info("Sending request to server.");

        var result = new RestFeedsClient(URI.create(serverUrl)).unsubscribeUser(user+"@"+userDomain,subUser+"@"+subUserDomain,pwd);
        System.out.println("Result: " + result);
    }
}

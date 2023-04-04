package sd2223.trab1.clients;

import sd2223.trab1.api.Message;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class PostMessageClient {
    private static Logger Log = Logger.getLogger(PostMessageClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 6) {
            System.err.println("Use: java trab1.clients.PostMessageClient url user pwd id msg domain");
            return;
        }

        String serverUrl = args[0];
        String user = args[1];
        String pwd = args[2];
        String id = args[3];
        String msg= args[4];
        String domain= args[5];

        Message m = new Message(Long.parseLong(id),user,domain,msg);
        Log.info("Sending request to server.");

        var result = new RestFeedsClient(URI.create(serverUrl)).postMessage(user+"@"+domain, pwd, m);
        System.out.println("Result: " + result);
    }
}

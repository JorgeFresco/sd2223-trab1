package sd2223.trab1.clients.soap;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class GetMessagesClient {
    private static Logger Log = Logger.getLogger(GetMessagesClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println("usage: serverUri user domain time");
            return;
        }

        String serverURI = args[0];
        String user = args[1];
        String domain = args[2];
        String time = args[3];

        var feeds = new SoapFeedsClient( URI.create( serverURI ));

        Log.info("Sending request to server.");

        var res = feeds.getMessages(user+"@"+domain,Long.parseLong(time));
        System.out.println("Result: " + res );
    }
}

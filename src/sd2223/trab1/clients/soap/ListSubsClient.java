package sd2223.trab1.clients.soap;


import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class ListSubsClient {
    private static Logger Log = Logger.getLogger(ListSubsClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 3) {
            System.err.println("usage: serverUri user domain");
            return;
        }

        String serverURI = args[0];
        String user = args[1];
        String domain = args[2];

        var feeds = new SoapFeedsClient( URI.create( serverURI ));

        Log.info("Sending request to server.");

        var res = feeds.listSubs(user+"@"+domain);
        System.out.println("Result: " + res );
    }
}

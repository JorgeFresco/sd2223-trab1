package sd2223.trab1.clients.soap;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class RemoveFromPersonalFeedClient {

    private static Logger Log = Logger.getLogger(RemoveFromPersonalFeedClient.class.getName());

    public static void main(String[] args) throws IOException {

        if (args.length != 5) {
            System.err.println("usage: serverUri user pwd mid domain");
            return;
        }

        String serverURI = args[0];
        String user = args[1];
        String pwd = args[2];
        String mid = args[3];
        String domain = args[4];

        var feeds = new SoapFeedsClient( URI.create( serverURI ));

        Log.info("Sending request to server.");

        var res = feeds.removeFromPersonalFeed(user+"@"+domain,Long.parseLong(mid),pwd);
        System.out.println("Result: " + res );
    }

}

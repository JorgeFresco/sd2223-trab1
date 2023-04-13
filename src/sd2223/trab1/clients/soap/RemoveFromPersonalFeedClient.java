package sd2223.trab1.clients.soap;

import sd2223.trab1.api.Message;
import sd2223.trab1.clients.rest.PostMessageClient;
import sd2223.trab1.clients.rest.RestFeedsClient;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class RemoveFromPersonalFeedClient {

    private static Logger Log = Logger.getLogger(RemoveFromPersonalFeedClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

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

        Log.info("Sending request to server.");


        var feeds = new SoapFeedsClient( URI.create( serverURI ));

        var res = feeds.removeFromPersonalFeed(user+"@"+domain,Long.parseLong(mid),pwd);
        System.out.println( res );
    }

}

package sd2223.trab1.clients.soap;

import sd2223.trab1.api.Message;
import sd2223.trab1.clients.rest.PostMessageClient;
import sd2223.trab1.clients.rest.RestFeedsClient;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class GetMessageClient {
    private static Logger Log = Logger.getLogger(PostMessageClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.err.println("usage: serverUri user domain mid");
            return;
        }

        String serverURI = args[0];
        String user = args[1];
        String domain = args[2];
        String mid = args[3];

        var feeds = new SoapFeedsClient( URI.create( serverURI ));

        var res = feeds.getMessage(user+"@"+domain,Long.parseLong(mid));
        System.out.println( res );
    }
}

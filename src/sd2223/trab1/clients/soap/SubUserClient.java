package sd2223.trab1.clients.soap;

import sd2223.trab1.clients.rest.PostMessageClient;
import sd2223.trab1.clients.rest.RestFeedsClient;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class SubUserClient {

    private static Logger Log = Logger.getLogger(SubUserClient.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 6) {
            System.err.println("usage: serverUri user userDomain subUser subUserDomain pwd");
            return;
        }

        String serverURI = args[0];
        String user = args[1];
        String userDomain = args[2];
        String subUser = args[3];
        String subUserDomain = args[4];
        String pwd = args[5];

        Log.info("Sending request to server.");

        var feeds = new SoapFeedsClient( URI.create( serverURI ));

        var res = feeds.subUser(user+"@"+userDomain,subUser+"@"+subUserDomain,pwd);
        System.out.println( res );
    }
}

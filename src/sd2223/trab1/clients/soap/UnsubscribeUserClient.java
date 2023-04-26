package sd2223.trab1.clients.soap;


import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

public class UnsubscribeUserClient {

    private static Logger Log = Logger.getLogger(UnsubscribeUserClient.class.getName());


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

        var feeds = new SoapFeedsClient( URI.create( serverURI ));

        Log.info("Sending request to server.");

        var res = feeds.unsubscribeUser(user+"@"+userDomain,subUser+"@"+subUserDomain,pwd);
        System.out.println("Result: " + res );
    }
}

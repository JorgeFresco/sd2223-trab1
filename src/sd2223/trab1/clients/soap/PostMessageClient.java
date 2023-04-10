package sd2223.trab1.clients.soap;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;

import java.net.URI;

public class PostMessageClient {
    public static void main(String[] args) {
        if( args.length != 6) {
            System.err.println( "usage: serverUri user pwd id msg domain");
            System.exit(0);

        }

        String serverURI = args[0];
        String user = args[1];
        String pwd = args[2];
        String id = args[3];
        String msg = args[4];
        String domain = args[5];


        var feeds = new SoapFeedsClient( URI.create( serverURI ));

        var res = feeds.postMessage(user+"@"+domain,pwd,new Message(Long.parseLong(id),user,domain,msg));
        System.out.println( res );
    }
}

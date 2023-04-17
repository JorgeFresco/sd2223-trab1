package sd2223.trab1.servers.soap;

import jakarta.xml.ws.Endpoint;
import sd2223.trab1.servers.util.Discovery;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SoapFeedsServer {

    public static final int PORT = 9090;
    public static final String SERVICE = "feeds";
    public static String SERVER_BASE_URI = "http://%s:%s/soap";

    private static Logger Log = Logger.getLogger(SoapFeedsServer.class.getName());

    public static void main(String[] args) throws Exception {

//		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
//		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
//		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
//		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");

        if (args.length != 2) {
            System.err.println("Use: java sd2223.trab1.servers.soap.SoapFeedsServer domain base");
            return;
        }
        String domain = args[0];
        int base = Integer.parseInt(args[1]);

        Log.setLevel(Level.INFO);

        String ip = InetAddress.getLocalHost().getHostAddress();
        String serverURI = String.format(SERVER_BASE_URI, ip, PORT);

        Endpoint.publish(serverURI.replace(ip, "0.0.0.0"), new SoapFeedsWebService(domain, base));

        String serviceName = domain+":"+SERVICE;
        Log.info(String.format("%s Soap Server ready @ %s\n", serviceName, serverURI));

        Discovery.getInstance().announce(serviceName, serverURI);
    }
}

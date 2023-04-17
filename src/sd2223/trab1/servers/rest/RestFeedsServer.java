package sd2223.trab1.servers.rest;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.servers.util.CustomLoggingFilter;
import sd2223.trab1.servers.util.Discovery;

public class RestFeedsServer {

    private static Logger Log = Logger.getLogger(RestFeedsServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8080;
    public static final String SERVICE = "feeds";
    public static final String INET_ADDR = "0.0.0.0";
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                System.err.println("Use: java sd2223.trab1.servers.rest.RestFeedsServer domain base");
                return;
            }
            String domain = args[0];
            long base = Long.parseLong(args[1]);

            ResourceConfig config = new ResourceConfig();
            config.register(new RestFeedsResource(domain, base));
            // config.register(CustomLoggingFilter.class);

            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            JdkHttpServerFactory.createHttpServer(URI.create(serverURI.replace(ip, INET_ADDR)), config);

            String serviceName = domain+":"+SERVICE;
            Log.info(String.format("%s Server ready @ %s\n", serviceName, serverURI));

            Discovery.getInstance().announce(serviceName, serverURI);

        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}
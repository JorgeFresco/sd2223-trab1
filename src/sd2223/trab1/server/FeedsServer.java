package sd2223.trab1.server;


import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import sd2223.trab1.server.util.Discovery;
import sd2223.trab1.server.resources.FeedsResource;
import sd2223.trab1.server.util.CustomLoggingFilter;


public class FeedsServer {
    
    
    private static Logger Log = Logger.getLogger(FeedsServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8080;
    public static final String SERVICE = "files";
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static void main(String[] args) {
        try {

            ResourceConfig config = new ResourceConfig();

            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);

            Discovery.getInstance().announce(ip, serverURI);

            config.register(FeedsResource.class);
            config.register(CustomLoggingFilter.class);

            JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

            Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));
        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}
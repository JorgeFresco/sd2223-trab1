package sd2223.trab1.servers.rest;

import java.net.InetAddress;
import java.net.URI;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import sd2223.trab1.servers.util.CustomLoggingFilter;
import sd2223.trab1.servers.util.Discovery;

public class RestUsersServer {

	private static Logger Log = Logger.getLogger(RestUsersServer.class.getName());



	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}

	public static final int PORT = 8080;
	public static final String SERVICE = "users";
	public static final String INET_ADDR = "0.0.0.0";
	private static final String SERVER_URI_FMT = "http://%s:%s/rest";

	public static void main(String[] args) {
		try {
			if (args.length != 1) {
				System.err.println("Use: java sd2223.trab1.servers.rest.RestUsersServer domain");
				return;
			}
			String domain = args[0];

			ResourceConfig config = new ResourceConfig();
			config.register(new RestUsersResource(domain));
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
package trab1.server.resources;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import trab1.api.Message;
import trab1.api.User;
import trab1.api.rest.FeedsService;
import trab1.api.rest.UsersService;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Singleton
public class FeedsResource implements FeedsService {

    private final Map<String,Map<Long, Message>> feeds = new HashMap<>();

    private static Logger Log = Logger.getLogger(UsersService.class.getName());


    @Override
    public long postMessage(String user, String pwd, Message msg) {
        Log.info("postMessage : user = " + user + "; pwd = " + pwd + "; msg = " + msg);

        // Checks if message data is valid
        if(msg == null || (Long)msg.getId() == null || msg.getUser() == null || msg.getDomain() == null || msg.getText() == null) {
            Log.info("Message object invalid.");
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        // TODO Checks if user exists and pwd matches user's password


        var name = user.split("@")[0];
        var feed = feeds.get(name);

        if (feed == null) {
            feed = new HashMap<>();
            feeds.put(name,feed);
        }

        feed.put(msg.getId(), msg);

        return msg.getId();
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        Log.info("removeFromPersonalFeed : user = " + user + "; pwd = " + pwd + "; mid = " + mid);

        // Checks if message ID is valid
        if((Long)mid == null) {
            Log.info("Message ID null.");
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        // TODO Checks if user exists and pwd matches user's password

        var name = user.split("@")[0];
        var feed = feeds.get(name);

        // Checks if the message exists
        if (feed == null || feed.containsKey(mid)) {
            Log.info("Message does not exist.");
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }

        feed.remove(mid);

    }

    @Override
    public Message getMessage(String user, long mid) {
        Log.info("getMessage : user = " + user + "; mid = " + mid);

        // Check if message ID is valid
        if((Long)mid == null) {
            Log.info("Message ID null.");
            throw new WebApplicationException( Response.Status.BAD_REQUEST );
        }

        var name = user.split("@")[0];
        var feed = feeds.get(name);

        // Checks if message exists
        if(feed == null || feed.get(mid) == null) {
            Log.info("Message does not exist.");
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }



        return feed.get(mid);
    }

    @Override
    public List<Message> getMessages(String user, long time) {
        return null;
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {

    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {

    }

    @Override
    public List<User> listSubs(String user) {
        return null;
    }

}

package sd2223.trab1.servers.java;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaFeeds implements Feeds {

    private final Map<String, Map<Long, Message>> feeds;
    private final Map <String, List<User>> subs;

    private long num_seq;


    private static Logger Log = Logger.getLogger(JavaFeeds.class.getName());

    public JavaFeeds() {
        feeds = new ConcurrentHashMap<>();
        subs = new ConcurrentHashMap<>();
        num_seq = 0;
    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        Log.info("postMessage : user = " + user + "; pwd = " + pwd + "; msg = " + msg);

        // Checks if user data is valid
        if (user == null || pwd == null) {
            Log.info("Name or password null.");
            return Result.error( Result.ErrorCode.BAD_REQUEST);
        }

        // Checks if message data is valid
        if (msg == null || msg.getUser() == null || msg.getDomain() == null || msg.getText() == null) {
            Log.info("Message object invalid.");
            return Result.error( Result.ErrorCode.BAD_REQUEST);
        }

        var feed = feeds.computeIfAbsent(user, k -> new HashMap<>());

        msg.setId(num_seq++);
        feed.put(msg.getId(), msg);

        return Result.ok(msg.getId());
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
         Log.info("removeFromPersonalFeed : user = " + user + "; pwd = " + pwd + "; mid = " + mid);
        /**
         // Checks if message ID is valid
         if ((Long) mid == null) {
         Log.info("Message ID null.");
         throw new WebApplicationException(Response.Status.BAD_REQUEST);
         }

         // TODO Checks if user exists and pwd matches user's password

         var name = user.split("@")[0];
         var feed = feeds.get(name);

         // Checks if the message exists
         if (feed == null || feed.containsKey(mid)) {
         Log.info("Message does not exist.");
         throw new WebApplicationException(Response.Status.NOT_FOUND);
         }

         feed.remove(mid);
         **/
        return null;
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        /**Log.info("getMessage : user = " + user + "; mid = " + mid);

         // Check if message ID is valid
         if ((Long) mid == null) {
         Log.info("Message ID null.");
         throw new WebApplicationException(Response.Status.BAD_REQUEST);
         }

         var name = user.split("@")[0];
         var feed = feeds.get(name);

         // Checks if message exists
         if (feed == null || feed.get(mid) == null) {
         Log.info("Message does not exist.");
         throw new WebApplicationException(Response.Status.NOT_FOUND);
         }


         return feed.get(mid);
         **/
        return null;
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
        /**
         Log.info("getMessage : user = " + user + "; time = " + time);

         if ((Long) time == null || user == null) {
         Log.info("Time or user null.");
         throw new WebApplicationException(Response.Status.BAD_REQUEST);
         }

         var name = user.split("@")[0];
         var result = new ArrayList<Message>();

         var l = (List<Message>) feeds.get(name).values();
         var s = subs.get(name);
         for ( User u:
         s) {
         l.addAll(feeds.get(u.getName()).values());
         }
         for (Message m : l) {
         if (m.getCreationTime() >= time) ;
         result.add(m);
         }

         return result;
         **/
        return null;
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        return null;
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        return null;
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        return null;
    }
}

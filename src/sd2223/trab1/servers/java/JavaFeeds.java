package sd2223.trab1.servers.java;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.clients.UsersClientFactory;
import sd2223.trab1.servers.util.Discovery;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaFeeds implements Feeds {

    Discovery discovery = Discovery.getInstance();

    public static final String USERS_SERVICE = "users";
    private final Map<String, Map<Long, Message>> feeds;
    private final Map <String, List<String>> subs;
    private long num_seq;


    private static final Logger Log = Logger.getLogger(JavaFeeds.class.getName());

    public JavaFeeds() {
        feeds = new ConcurrentHashMap<>();
        subs = new ConcurrentHashMap<>();
        num_seq = 0;

    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        Log.info("postMessage : user = " + user + "; pwd = " + pwd + "; msg = " + msg);

        // Checks if message data is valid
        if (msg == null || msg.getUser() == null || msg.getDomain() == null || msg.getText() == null) {
            Log.info("Message object invalid.");
            return Result.error( Result.ErrorCode.BAD_REQUEST);
        }

        // Checks if the user and the message name and domain match
        String[] parts = user.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (!name.equals(msg.getUser()) || !domain.equals(msg.getDomain())) {
            Log.info("Message user or domain do not match the provided user.");
            return Result.error( Result.ErrorCode.BAD_REQUEST);
        }

        // Checks if the user is valid, exists and the password is correct
        Result<User> res = checkUser(user, pwd);
        if (!res.isOK()) {
            Log.info("User either is not valid, doesn't exist or the password is incorrect");
            return Result.error(res.error());
        }

        var feed = feeds.computeIfAbsent(user, k -> new ConcurrentHashMap<>());

        msg.setId(num_seq++);
        feed.put(msg.getId(), msg);


        for (var l : subs.entrySet()) {
            if (l.getValue().contains(user)) {
                var msgs = feeds.computeIfAbsent(l.getKey(), k -> new ConcurrentHashMap<>());
                msgs.put(msg.getId(), msg);
            }
        }
        return Result.ok(msg.getId());
   }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
         Log.info("removeFromPersonalFeed : user = " + user + "; pwd = " + pwd + "; mid = " + mid);

        // Checks if the user is valid, exists and the password is correct
        Result<User> res = checkUser(user, pwd);
        if (!res.isOK()) {
            Log.info("User either is not valid, doesn't exist or the password is incorrect");
            return Result.error(res.error());
        }

         // Checks if message ID is valid
         if ((Long) mid == null) {
             Log.info("Message ID null.");
             return Result.error(Result.ErrorCode.BAD_REQUEST);
         }

         var feed = feeds.get(user);

         // Checks if the message exists
         if (feed == null || !feed.containsKey(mid)) {
             Log.info("Message does not exist.");
             return Result.error(Result.ErrorCode.NOT_FOUND);
         }

         feed.remove(mid);

        return Result.ok();
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        Log.info("getMessage : user = " + user + "; mid = " + mid);

        // Check if the user and message ID are valid
        if (user == null || (Long) mid == null) {
            Log.info("User or message ID is null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        // Checks if the user exists
        if (!userExists(user)) {
            Log.info("User does not exist.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

         var feed = feeds.get(user);

         // Checks if message exists
         if (feed == null || feed.get(mid) == null) {
             Log.info("Message does not exist.");
             return Result.error(Result.ErrorCode.NOT_FOUND);
         }

         return Result.ok(feed.get(mid));
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
         Log.info("getMessage : user = " + user + "; time = " + time);

        if ((Long) time == null || user == null) {
            Log.info("Time or user null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

         // Checks if the user exists
         if (!userExists(user)) {
             Log.info("User does not exist.");
             return Result.error(Result.ErrorCode.NOT_FOUND);
         }

         Map<Long, Message> map = feeds.get(user);
         if (map == null) {
             return Result.ok(new ArrayList<>());
         }

         return Result.ok(feeds.get(user).values().stream().filter((msg) -> msg.getCreationTime() > time).toList());
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {
        Log.info("subUser : user = " + user + "; userSub = " + userSub + "; pwd= " +pwd);

        // Checks if the user is valid, exists and the password is correct
        Result<User> res = checkUser(user, pwd);
        if (!res.isOK()) {
            Log.info("User either is not valid, doesn't exist or the password is incorrect");
            return Result.error(res.error());
        }

        if (userSub == null) {
            Log.info("userSub null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        // Checks if the userSub exists
        if (!userExists(userSub)) {
            Log.info("User to subscribe does not exist.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        var subList = subs.computeIfAbsent(user, k -> new ArrayList<>());
        if (!subList.contains(userSub)) subList.add(userSub);

        var feedUser = feeds.computeIfAbsent(user,k -> new ConcurrentHashMap<>());

        var name= userSub.split("@")[0];
        var domain = userSub.split("@")[1];
        var feedSub = feeds.get(userSub);
        if(feedSub != null) {
            for (var m : feedSub.values()) {
                if (name.equals(m.getUser()) && domain.equals(m.getDomain())) {
                    feedUser.put(m.getId(), m);
                }
            }
        }

        return Result.ok();
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {
        Log.info("unsubscribeUser : user = " + user + "; userSub = " + userSub + "; pwd= " +pwd);

        // Checks if the user is valid, exists and the password is correct
        Result<User> res = checkUser(user, pwd);
        if (!res.isOK()) {
            Log.info("User either is not valid, doesn't exist or the password is incorrect");
            return Result.error(res.error());
        }

        // Checks if the user exists
        if (!userExists(user)) {
            Log.info("User does not exist.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        // Checks if the userSub exists
        if (!userExists(userSub)) {
            Log.info("User to unsubscribe does not exist.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        subs.get(user).remove(userSub);

        var l = feeds.get(user);
        List<Message> msgs;
        if (l != null) {
            msgs = l.values().stream().toList();
            for(var m : msgs) {
                var name = userSub.split("@")[0];
                var domain = userSub.split("@")[1];
                if (name.equals(m.getUser()) && domain.equals(m.getDomain())) {
                    l.remove(m.getId());
                }
            }
        }
        return Result.ok();
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        Log.info("listSubs : user = " + user);
        if (user == null) {
            Log.info("User null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        // Checks if the user exists
        if (!userExists(user)) {
            Log.info("User does not exist.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        List<String> subList = subs.get(user);

        return subList != null ? Result.ok(subList) : Result.ok(new LinkedList<>());
    }

    @Override
    public Result<Void> deleteFeed(String user) {
        Log.info("deleteFeed : user = " + user);
        feeds.remove(user);
        subs.remove(user);
        return Result.ok();
    }

    /**
     * Checks if the user provided is valid, exists and the password is correct
     * @param user username and domain
     * @param pwd user password
     * @return the result of the getUser operation
     */
    private Result<User> checkUser(String user, String pwd) {
        String[] parts = user.split("@");
        String name = parts[0];
        String domain = parts[1];

        String serviceName = domain + ":" + USERS_SERVICE;
        URI[] uris = discovery.knownUrisOf(serviceName, 1);
        return UsersClientFactory.get(uris[uris.length-1]).getUser(name, pwd);
    }

    private boolean userExists(String user) {
        String[] parts = user.split("@");
        String name = parts[0];
        String domain = parts[1];

        String serviceName = domain + ":" + USERS_SERVICE;
        URI[] uris = discovery.knownUrisOf(serviceName, 1);
        Result<Boolean> res = UsersClientFactory.get(uris[uris.length-1]).userExists(name);
        return res.isOK() && res.value();
    }

}

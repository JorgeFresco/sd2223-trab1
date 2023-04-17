package sd2223.trab1.servers.java;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.clients.FeedsClientFactory;
import sd2223.trab1.clients.UsersClientFactory;
import sd2223.trab1.servers.util.Discovery;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaFeeds implements Feeds {

    Discovery discovery = Discovery.getInstance();

    public static final String USERS_SERVICE = "users";

    public static final String FEEDS_SERVICE = "feeds";

    private final Map<String, Map<Long, Message>> feeds;
    private final Map <String, List<String>> subs;
    private long num_seq;
    private final String domain;
    private final long base;


    private static final Logger Log = Logger.getLogger(JavaFeeds.class.getName());

    public JavaFeeds(String domain, long base) {
        feeds = new ConcurrentHashMap<>();
        subs = new ConcurrentHashMap<>();
        num_seq = 0;
        this.domain = domain;
        this.base = base;

    }

    @Override
    public Result<Long> postMessage(String user, String pwd, Message msg) {
        Log.info("postMessage : user = " + user + "; pwd = " + pwd + "; msg = " + msg);

        // Checks if message data is valid
        if (msg == null || msg.getUser() == null || msg.getDomain() == null || msg.getText() == null) {
            Log.info("Message object invalid.");
            return Result.error( Result.ErrorCode.BAD_REQUEST);
        }

        // Checks if the user is valid, exists and the password is correct
        Result<User> res = checkUser(user, pwd);
        if (!res.isOK()) {
            Log.info("User either is not valid, doesn't exist or the password is incorrect");
            return Result.error(res.error());
        }

        // Checks if the user and the message name and domain match
        String[] parts = user.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (!name.equals(msg.getUser()) || !domain.equals(msg.getDomain())) {
            Log.info("Message user or domain do not match the provided user.");
            return Result.error( Result.ErrorCode.BAD_REQUEST);
        }

        var feed = feeds.computeIfAbsent(user, k -> new ConcurrentHashMap<>());

        msg.setId(getNextMsgId());
        feed.put(msg.getId(), msg);

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
        if (user == null) {
            Log.info("User or message ID is null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        // If user is a remote user (from other domain)
        String userDomain = user.split("@")[1];
        if (!userDomain.equals(domain)) {
            String serviceName = userDomain + ":" + FEEDS_SERVICE;
            URI[] uris = discovery.knownUrisOf(serviceName, 1);
            return FeedsClientFactory.get(uris[uris.length-1]).getMessage(user, mid);
        }

        // Checks if the user exists
        if (!userExists(user)) {
            Log.info("User does not exist.");
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

         var msg = getFeed(user).get(mid);

         // Checks if message exists
         if (msg == null) {
             Log.info("Message does not exist.");
             return Result.error(Result.ErrorCode.NOT_FOUND);
         }

         return Result.ok(msg);
    }

    @Override
    public Result<List<Message>> getMessages(String user, long time) {
         Log.info("getMessage : user = " + user + "; time = " + time);

        if (user == null) {
            Log.info("Time or user null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        // If user is a remote user (from other domain)
        String userDomain = user.split("@")[1];
        if (!userDomain.equals(domain)) {
            String serviceName = userDomain + ":" + FEEDS_SERVICE;
            URI[] uris = discovery.knownUrisOf(serviceName, 1);
            return FeedsClientFactory.get(uris[uris.length-1]).getMessages(user, time);
        }

         // Checks if the user exists
         if (!userExists(user)) {
             Log.info("User does not exist.");
             return Result.error(Result.ErrorCode.NOT_FOUND);
         }
         return Result.ok(getFeed(user).values().stream().filter((msg) -> msg.getCreationTime() > time).toList());
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
        for (var l : subs.values()) {
            l.remove(user);
        }

        return Result.ok();
    }

    @Override
    public Result<Map<Long, Message>> getPersonalFeed(String user) {
        Log.info("getPersonalFeed : user = " + user);

        if (!feeds.containsKey(user))
            return Result.error(Result.ErrorCode.NOT_FOUND);

        return Result.ok(feeds.get(user));
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

    /**
     * Checks if the user exists
     * @param user username and domain of the user
     * @return true if the user exists or false otherwise
     */
    private boolean userExists(String user) {
        String[] parts = user.split("@");
        String name = parts[0];
        String domain = parts[1];

        String serviceName = domain + ":" + USERS_SERVICE;
        URI[] uris = discovery.knownUrisOf(serviceName, 1);
        Result<Boolean> res = UsersClientFactory.get(uris[uris.length-1]).userExists(name);
        return res.isOK() && res.value();
    }

    /**
     * Calculates and returns the next message id
     * @return the next message id
     */
    private long getNextMsgId() {
        return (num_seq++) * 256 + base;
    }

    private Map<Long,Message> getFeed(String user) {
        Map<Long, Message> msgList;

        // Initial msg list
        Map<Long, Message> userFeed = feeds.get(user);
        if (userFeed != null) msgList = new HashMap<>(userFeed);
        else msgList = new HashMap<>();

        // Add to the list all the messages of his subs
        List<String> userSubs = subs.get(user);
        if(userSubs != null) {
            for (String sub: userSubs) {
                Map<Long, Message> subMsgs = null;
                String subDomain = sub.split("@")[1];
                if (subDomain.equals(domain)) subMsgs = feeds.get(sub);
                else {
                    String serviceName = subDomain + ":" + FEEDS_SERVICE;
                    URI[] uris = discovery.knownUrisOf(serviceName, 1);
                    Result<Map<Long, Message>> res = FeedsClientFactory.get(uris[uris.length-1]).getPersonalFeed(sub);
                    if(res.isOK()) subMsgs = res.value();
                }

                if(subMsgs != null) {
                    msgList.putAll(subMsgs);
                    }
                }
            }
        return msgList;
    }

}

package sd2223.trab1.servers.java;

import sd2223.trab1.api.Message;
import sd2223.trab1.api.java.Feeds;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.servers.util.Discovery;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class JavaFeeds implements Feeds {

    Discovery discovery = Discovery.getInstance();

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

        var subList = subs.get(user);
        for( String u : subs.get(user)){
            var v= feeds.get(u);
            if(v== null){
                var l = new ConcurrentHashMap<Long,Message>();
                l.put(msg.getId(), msg);
                feeds.put(u,l);
            }
            else
                v.put(msg.getId(), msg);
        }
        return Result.ok(msg.getId());
    }

    @Override
    public Result<Void> removeFromPersonalFeed(String user, long mid, String pwd) {
         Log.info("removeFromPersonalFeed : user = " + user + "; pwd = " + pwd + "; mid = " + mid);

         // Checks if message ID is valid
         if ((Long) mid == null) {
         Log.info("Message ID null.");
         return Result.error(Result.ErrorCode.BAD_REQUEST);
         }

         var feed = feeds.get(user);

         // Checks if the message exists
         if (feed == null || feed.containsKey(mid)) {
         Log.info("Message does not exist.");
         return Result.error(Result.ErrorCode.NOT_FOUND);
         }

         feed.remove(mid);

        return Result.ok();
    }

    @Override
    public Result<Message> getMessage(String user, long mid) {
        Log.info("getMessage : user = " + user + "; mid = " + mid);

        // Check if message ID is valid
        if ((Long) mid == null) {
            Log.info("Message ID is null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
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

         Map<Long, Message> map = feeds.get(user);
         if (map == null) {
             Log.info("User feed does not exist.");
             return Result.error(Result.ErrorCode.NOT_FOUND);
         }

         List<Message> list = feeds.get(user).values().stream().toList();
         return Result.ok(list.stream().filter((msg) -> msg.getCreationTime() >= time).toList());
    }

    @Override
    public Result<Void> subUser(String user, String userSub, String pwd) {

        Log.info("subUser : user = " + user + "; userSub = " + userSub + "; pwd= " +pwd);

        if (userSub == null || user == null) {
            Log.info("userSub or user null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }


        var subList = subs.computeIfAbsent(user, k -> new ArrayList<>());
        subList.add(userSub);

        var t = feeds.get(userSub);
        var t1 = feeds.get(user);
        if( t1 == null){
            t1 = new ConcurrentHashMap<Long, Message>();
        }

        var name = userSub.split("@")[0];
        var domain = userSub.split("@")[1];
        if(t!=null) {
            for (var m : t.values()) {
                if (m.getUser().equals(name) && m.getDomain().equals(domain)) {
                    t1.put(m.getId(), m);
                }
            }
        }
        return Result.ok();
    }

    @Override
    public Result<Void> unsubscribeUser(String user, String userSub, String pwd) {

        Log.info("unsubscribeUser : user = " + user + "; userSub = " + userSub + "; pwd= " +pwd);

        if (userSub == null || user == null) {
            Log.info("userSub or user null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        var sub = subs.get(user);
        sub.remove(userSub);

        return Result.ok();
    }

    @Override
    public Result<List<String>> listSubs(String user) {
        if (user == null) {
            Log.info("User null.");
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        List<String> sub = subs.get(user);
            return sub != null ? Result.ok(subs.get(user)) : Result.ok(new LinkedList<>());

    }

}

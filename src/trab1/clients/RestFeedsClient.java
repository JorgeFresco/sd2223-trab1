package trab1.clients;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import trab1.api.Message;
import trab1.api.User;
import trab1.api.rest.FeedsService;
import trab1.api.rest.UsersService;
import trab1.clients.RestClient;

import java.net.URI;
import java.util.List;

public class RestFeedsClient extends RestClient implements FeedsService {

    final WebTarget target;

    public RestFeedsClient(URI serverURI) {
        super( serverURI );
        target = client.target( serverURI ).path( UsersService.PATH );
    }

    private Long clt_PostMessage( String user,String pwd,Message msg) {

        var name = user.split("@")[0];
        Response r = target.path( name)
                .queryParam(UsersService.PWD, pwd).request()
                .accept(MediaType.APPLICATION_JSON).get();



        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() ){
            r= target.request()
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(msg, MediaType.APPLICATION_JSON));
            return r.readEntity(Long.class);}
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return null;
    }

    private void clt_removeFromPersonalFeed(String user, long mid, String pwd) {

        var name = user.split("@")[0];
        Response r = target.path( name)
                    .queryParam(UsersService.PWD, pwd).request()
                    .get();

        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() )
            r= target.path( name+"/"+mid)
                    .request().delete();
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

    }
    private Message clt_getMessage(String user, long mid) {
        var name = user.split("@")[0];
        Response r= target.path( name+"/"+mid)
                .request()
                .get();

        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() )
            return r.readEntity(Message.class);
        else{
            System.out.println("Error, HTTP error status: " + r.getStatus() );
            return null;}
    }

    private List<Message> clt_getMessages(String user, long time) {
        var name = user.split("@")[0];
        Response r = target.path("/").queryParam(FeedsService.TIME, time).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() ) {
            var messages = r.readEntity(new GenericType<List<Message>>() {});
            System.out.println("Success: (" + messages.size() + " users)");
            messages.stream().forEach( u -> System.out.println( u));
        } else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return null;
    }

    private void clt_subUser(String user, String userSub, String pwd) {
        var name = user.split("@")[0];
        Response r = target.path( name)
                .queryParam(UsersService.PWD, pwd).request()
                .get();

        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() )
            r= target.path( name+"/"+userSub)
                    .request().post(Entity.entity(userSub, MediaType.APPLICATION_JSON));
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );
    }

    private void clt_unsubscribeUser(String user, String userSub, String pwd) {
        var name = user.split("@")[0];
        Response r = target.path( name)
                .queryParam(UsersService.PWD, pwd).request()
                .get();

        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() )
            r= target.path( name+"/"+userSub)
                    .request().delete();
        else
            System.out.println("Error, HTTP error status: " + r.getStatus() );
    }

    private List<User> clt_listSubs(String user) {
        Response r = target.path("/").request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if( r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity() ) {
            var users = r.readEntity(new GenericType<List<User>>() {});
            System.out.println("Success: (" + users.size() + " users)");
            users.stream().forEach( u -> System.out.println( u));
        } else
            System.out.println("Error, HTTP error status: " + r.getStatus() );

        return null;
    }


    @Override
    public long postMessage(String user, String pwd, Message msg) {
        return super.reTry( () -> clt_PostMessage(user, pwd, msg) );
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        //super.reTry(() -> clt_removeFromPersonalFeed(user, mid, pwd));
    }

    @Override
    public Message getMessage(String user, long mid) {

        return super.reTry( () -> clt_getMessage(user, mid) );
    }



    @Override
    public List<Message> getMessages(String user, long time) {
        return super.reTry( () -> clt_getMessages(user, time) );
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
       // super.reTry( () -> clt_subUser(user, userSub, pwd) );
    }



    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
       // super.reTry( () -> clt_unsubscribeUser(user,userSub,pwd));
    }


    @Override
    public List<User> listSubs(String user) {

        return super.reTry( () -> clt_listSubs(user));
    }

}

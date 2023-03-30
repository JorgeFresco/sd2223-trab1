package sd2223.trab1.server.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import sd2223.trab1.api.User;
import sd2223.trab1.api.rest.UsersService;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Singleton
public class UsersResource implements UsersService {

	private final Map<String, User> users;

	private static Logger Log = Logger.getLogger(UsersResource.class.getName());

	public UsersResource() {

		users = new ConcurrentHashMap<>();
	}

	@Override
	public String createUser(User user) {
		Log.info("createUser : " + user);

		// Check if user data is valid
		if(user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null) {
			Log.info("User object invalid.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		// Insert user, checking if name already exists
		if( users.putIfAbsent(user.getName(), user) != null ) {
			Log.info("User already exists.");
			throw new WebApplicationException( Status.CONFLICT );
		}

		return user.getName()+"@"+user.getDomain();
	}

	@Override
	public User getUser(String name, String pwd) {
		Log.info("getUser : user = " + name + "; pwd = " + pwd);

		// Check if user is valid
		if(name == null || pwd == null) {
			Log.info("Name or Password null.");
			throw new WebApplicationException( Status.BAD_REQUEST );
		}

		User user = users.get(name);
		// Check if user exists
		if( user == null ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Status.NOT_FOUND );
		}

		//Check if the password is correct
		if( !user.getPwd().equals( pwd)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Status.FORBIDDEN );
		}

		return user;
	}

	@Override
	public User updateUser(String name, String pwd, User user) {
		Log.info("updateUser : user = " + name + "; pwd = " + pwd + " ; user = " + user);

		// Check if user is valid
		if(name == null || pwd == null) {
			Log.info("Name or password null.");
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		var u = users.get(name);

		// Check if user exists
		if( u == null ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}

		//Check if the password is correct
		if( !u.getPwd().equals( pwd)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Response.Status.FORBIDDEN );
		}

		users.put(name,user);

		return user;
	}

	@Override
	public User deleteUser(String name, String pwd) {
		Log.info("deleteUser : user = " + name + "; pwd = " + pwd);

		// Check if user is valid
		if(name == null || pwd == null) {
			Log.info("UserId or password null.");
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		var user = users.get(name);

		// Check if user exists
		if( user == null ) {
			Log.info("User does not exist.");
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}

		// Check if the password is correct
		if( !user.getPwd().equals( pwd)) {
			Log.info("Password is incorrect.");
			throw new WebApplicationException( Response.Status.FORBIDDEN );
		}

		users.remove(name);

		return user;
	}

	@Override
	public List<User> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);
		var searchedUsers = new ArrayList<User>();
		for (User user: users.values()) {
			if(user.getName().toLowerCase().contains(pattern.toLowerCase()))
				searchedUsers.add(new User(user.getName(),"",user.getDomain(), user.getDisplayName()));
		}

		return searchedUsers;
	}

}

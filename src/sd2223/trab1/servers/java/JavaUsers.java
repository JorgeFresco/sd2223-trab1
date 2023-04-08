package sd2223.trab1.servers.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import sd2223.trab1.api.User;
import sd2223.trab1.api.java.Result;
import sd2223.trab1.api.java.Result.ErrorCode;
import sd2223.trab1.api.java.Users;

public class JavaUsers implements Users {
	private final Map<String,User> users = new ConcurrentHashMap<>();

	private static final Logger Log = Logger.getLogger(JavaUsers.class.getName());

	@Override
	public Result<String> createUser(User user) {
		Log.info("createUser : " + user);

		// Check if user data is valid
		if(isUserInvalid(user)) {
			Log.info("User object invalid.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}

		// Insert user, checking if name already exists
		if( users.putIfAbsent(user.getName(), user) != null ) {
			Log.info("User already exists.");
			return Result.error( ErrorCode.CONFLICT);
		}

		return Result.ok( user.getName()+"@"+user.getDomain()  );
	}

	@Override
	public Result<User> getUser(String name, String pwd) {
		Log.info("getUser : user = " + name + "; pwd = " + pwd);

		// Check if user is valid
		if(isUserInvalid(name, pwd)) {
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}

		// Checks if the user exists
		User user = users.get(name);
		if( user == null ) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}

		//Check if the password is correct
		if( !user.getPwd().equals( pwd)) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}

		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String name, String pwd, User user) {
		Log.info("updateUser : name = " + name + "; pwd = " + pwd + "; user = " + user);

		if (!name.equals(user.getName())) {
			Log.info("The name provided and the updated user's name don't match");
			return Result.error( ErrorCode.BAD_REQUEST);
		}

		// Check if user is valid and password is correct
		Result<User> res = getUser(name, pwd);
		if (!res.isOK()) return Result.error(res.error());
		User u = res.value();


		// Name and Domain cannot be changed
		user.setDomain(u.getDomain());

		// Checks for null properties
		if (user.getPwd() == null)
			user.setPwd(u.getPwd());
		if (user.getDisplayName() == null)
			user.setDisplayName(u.getDisplayName());

		users.put(name,user);

		return Result.ok(user);
	}

	@Override
	public Result<User> deleteUser(String name, String pwd) {
		Log.info("deleteUser : user = " + name + "; pwd = " + pwd);

		Result<User> res = getUser(name, pwd);
		if (!res.isOK()) return Result.error(res.error());

		users.remove(name);

		return Result.ok(res.value());
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		Log.info("searchUsers : pattern = " + pattern);
		var searchedUsers = new ArrayList<User>();
		for (User user: users.values()) {
			if(user.getName().toLowerCase().contains(pattern.toLowerCase()))
				searchedUsers.add(new User(user.getName(),"",user.getDomain(), user.getDisplayName()));
		}
		return Result.ok(searchedUsers);
	}

	private boolean isUserInvalid( User user ) {
		return user.getName() == null || user.getPwd() == null || user.getDisplayName() == null || user.getDomain() == null;
	}

	private boolean isUserInvalid(String name, String pwd) {
		return name == null || pwd == null;
	}
}

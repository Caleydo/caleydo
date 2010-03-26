package daemon;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class UserManager {
	
	private HashMap<String, User> users; 
	
	public UserManager(){
		this.users = new HashMap<String, User>(); 
	}
	
	/**
	 * Checks the hashmap for a user with a given pointerID. 
	 * If the user is not found in the hashmap, a new user with 
	 * the given pointerID is generated. 
	 * @param pointerID The pointerID uniquely associated with the user. 
	 * @return Returns the user in the hashmap or a newly created one. 
	 */
	public User getUser(String pointerID){
		User user = this.users.get(pointerID); 
		if(user == null){
			System.out.println("Creating new user " + pointerID); 
			user = new User(pointerID); 
			this.users.put(pointerID, user); 
		}
		return user; 
	}
	
	/**
	 * Checks all users whether they are associated with an incoming 
	 * modified application. 
	 * If so, they will be added to the list of affected users. 
	 * @param modifiedApp The application that has been modified (e.g. scrolled). 
	 * @return Returns a list of users who have the given application associated 
	 * (as source or target application). 
	 */
	public List<User> getAffectedUsers(Application modifiedApp){
		List<User> usersList = new ArrayList<User>(); 
		for (Entry<String, User> e : users.entrySet()) {
			User user = e.getValue(); 
			if(user.hasApplicationAccessible(modifiedApp)){
				usersList.add(user); 
			}
		}
		return usersList; 
	}
	
	

}

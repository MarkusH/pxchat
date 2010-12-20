package pxchat.net.protocol.frames;

import java.util.HashMap;

/**
 * This frame is used to transfer the current user list to the clients.
 * 
 * @author Robert Waury
 */
public class UserListFrame extends Frame {

	private static final long serialVersionUID = -1945738110878587604L;
	
	/**
	 * A mapping of session id to user name of the connected clients.
	 */
	private HashMap<Integer, String> userlist;
	
	/**
	 * Constructs a new user list frame with the specified list.
	 * 
	 * @param userList The user list to transfer
	 */
	public UserListFrame(HashMap<Integer, String> userList) {
		this.id = Frame.ID_USERLIST;
		this.userlist = userList;
	}

	/**
	 * @return The user list
	 */
	public HashMap<Integer, String> getUserlist() {
		return userlist;
	}
	
}

package pxchat.net.protocol.frames;

import java.util.HashMap;

/**
 * @author markus
 *
 */
public class UserListFrame extends Frame {

	private HashMap<Integer, String> userlist;
	
	public UserListFrame(HashMap<Integer, String> userList) {
		this.id = Frame.ID_USERLIST;
		this.userlist = userList;
	}

	public HashMap<Integer, String> getUserlist() {
		return userlist;
	}
	
}

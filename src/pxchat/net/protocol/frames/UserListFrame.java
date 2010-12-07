package pxchat.net.protocol.frames;

import java.util.HashMap;

public class UserListFrame extends Frame {

	private HashMap<Integer,String> userlist;
	
	UserListFrame() {
		this.id = Frame.ID_USERLIST;
		this.userlist = new HashMap<Integer, String>();
	}

	public HashMap<Integer, String> getUserlist() {
		return userlist;
	}

	public void addUser(Integer uid, String name) {
		this.userlist.put(uid, name);
	}
	
	public void removeUser(Integer uid) {
		this.userlist.remove(uid);
	}
	
}

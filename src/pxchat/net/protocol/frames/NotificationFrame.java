package pxchat.net.protocol.frames;

public class NotificationFrame extends Frame {
	
	public final static int JOIN = 1;
	public final static int LEAVE = 2;
	public final static int AUTH_FAIL = 3;
	public final static int TIMEOUT = 4;
	public final static int VERSION_FAIL = 5; 
	
	private int type;
	private String username;

	public NotificationFrame(int type) {
		this.id = ID_NOTIFICATION;
		this.type = type;
	}
	
	public NotificationFrame(int type, String username) {
		this.id = ID_NOTIFICATION;
		this.type = type;
		this.username = username;
	}

	public int getType() {
		return type;
	}

	public String getUsername() {
		return username;
	}

}

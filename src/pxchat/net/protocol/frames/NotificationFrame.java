package pxchat.net.protocol.frames;

/**
 * This frame is used to send notifications from the server to clients. A
 * notification does not contain the complete text but only a user name that is
 * inserted in the resulting string by the client. This enables the client to
 * translate the notifications.
 * 
 * @author Markus Holtermann
 */
public class NotificationFrame extends Frame {

	private static final long serialVersionUID = 8648909122859755047L;

	/**
	 * A user joined the chat.
	 */
	public final static int JOIN = 1;
	
	/**
	 * A user left the chat.
	 */
	public final static int LEAVE = 2;
	
	/**
	 * The authentication failed.
	 */
	public final static int AUTH_FAIL = 3;
	
	/**
	 * A timeout was detected.
	 */
	public final static int TIMEOUT = 4;
	
	/**
	 * The version of the client is not compatible with the server version.
	 */
	public final static int VERSION_FAIL = 5;

	/**
	 * The type of this notification, one of the constants above.
	 */
	private int type;
	
	/**
	 * The user name associated with this notification.
	 */
	private String username;

	/**
	 * Constructs a new notification frame with the specified type.
	 * 
	 * @param type The type of the notification
	 */
	public NotificationFrame(int type) {
		this.id = ID_NOTIFICATION;
		this.type = type;
	}

	/**
	 * Constructs a new notification frame with the specified type and user name.
	 * 
	 * @param type The type of the notification
	 * @param username The user name of the notification
	 */
	public NotificationFrame(int type, String username) {
		this.id = ID_NOTIFICATION;
		this.type = type;
		this.username = username;
	}

	/**
	 * @return The type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return The user name
	 */
	public String getUsername() {
		return username;
	}

}

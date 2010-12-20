package pxchat.net.protocol.frames;

/**
 * This frame is used by clients to authenticate with a server. The credentials
 * consist of a user name and a matching password.
 * 
 * @author Robert Waury
 */
public class AuthenticationFrame extends Frame {

	private static final long serialVersionUID = 8419764599376913360L;

	/**
	 * The login name.
	 */
	private String username;

	/**
	 * The login password.
	 */
	private String password;

	/**
	 * Constructs a new authentication frame with the specified user name and
	 * password.
	 * 
	 * @param username The user's login name
	 * @param password The user's password
	 */
	public AuthenticationFrame(String username, String password) {
		this.username = username;
		this.password = password;
		this.id = Frame.ID_AUTH;
	}

	/**
	 * Returns the password of this frame.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the user name of this frame.
	 * 
	 * @return the user name
	 */
	public String getUsername() {
		return username;
	}

}

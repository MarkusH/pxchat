package pxchat.net.protocol.frames;

public class AuthenticationFrame extends Frame {

	private String username;
	private String password;

	public AuthenticationFrame(String username, String password) {
		this.username = username;
		this.password = password;
		this.id = Frame.ID_AUTH;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

}

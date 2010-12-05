package pxchat.net.protocol.frames;

public class AuthentificationFrame extends Frame {

	private String username;
	private String password;

	AuthentificationFrame(String username, String password) {
		this.username = username;
		this.password = password;
		this.id = Frame.ID_AUTH;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}

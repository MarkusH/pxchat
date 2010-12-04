package pxchat.net.protocol.frames;

public class MessageFrame extends Frame {

	private String message;
	private int sessionId;

	public MessageFrame(String message) {
		this.id = Frame.ID_MSG;
		this.message = message;
		this.sessionId = 0;
	}

	public String getMessage() {
		return message;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

}

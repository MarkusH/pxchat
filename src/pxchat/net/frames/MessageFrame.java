package pxchat.net.frames;

public class MessageFrame extends Frame {

	private String message;
	
	public MessageFrame(String message) {
		this.id = Frame.ID_MSG;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}

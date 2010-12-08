package pxchat.net.protocol.frames;

public class NotificationFrame extends Frame {
	
	private String message;

	public NotificationFrame(String message) {
		this.id = ID_NOTIFICATION;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}

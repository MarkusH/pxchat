package pxchat.net.protocol.frames;

public class AutomatedMessageFrame extends MessageFrame {
	
	public AutomatedMessageFrame(String message) {
		super(message);
		this.id = ID_AUTO;
	}

}

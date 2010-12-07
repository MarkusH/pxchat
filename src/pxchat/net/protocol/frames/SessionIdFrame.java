package pxchat.net.protocol.frames;

public class SessionIdFrame extends Frame {
	
	int sessionId;
	
	SessionIdFrame() {
		this.id = Frame.ID_SID;
	}
	

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

}

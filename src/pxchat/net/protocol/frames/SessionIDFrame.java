package pxchat.net.protocol.frames;

/**
 * This frame is used to transfer the session id of a newly connected client to
 * it.
 * 
 * @author Markus Döllinger
 */
public class SessionIDFrame extends Frame {

	private int sessionID;

	/**
	 * Constructs a new session id frame with the specified session id.
	 * 
	 * @param sessionID The session id for the client
	 */
	public SessionIDFrame(int sessionID) {
		this.id = Frame.ID_SID;
		this.sessionID = sessionID;
	}

	/**
	 * @return the sessionID
	 */
	public int getSessionID() {
		return sessionID;
	}

}

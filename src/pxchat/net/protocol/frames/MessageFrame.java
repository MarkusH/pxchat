package pxchat.net.protocol.frames;

/**
 * This frame is used to transfer text messages. The message is represented by a
 * String, the author is tracked by storing his session id. The session id is
 * set by the server just before the message is forwarded to the other clients.
 * 
 * @author Robert Waury
 */
public class MessageFrame extends Frame {

	private static final long serialVersionUID = 3679781431957127182L;

	/**
	 * The text message.
	 */
	private String message;

	/**
	 * The sender's session id
	 */
	private int sessionId;

	/**
	 * Constructs a new message frame with the specified text.
	 * 
	 * @param message The text message
	 */
	public MessageFrame(String message) {
		this.id = Frame.ID_MSG;
		this.message = message;
		this.sessionId = 0;
	}

	/**
	 * Returns the text message of this frame.
	 * 
	 * @return The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the sender's session id.
	 * 
	 * @return The session id
	 */
	public int getSessionId() {
		return sessionId;
	}

	/**
	 * Sets the session id of this frame.
	 * 
	 * @param sessionId The new session id
	 */
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

}

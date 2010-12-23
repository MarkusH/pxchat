package pxchat.net;

import java.util.HashMap;

import pxchat.net.protocol.frames.NotificationFrame;

/**
 * <p>
 * Together with the {@link WhiteboardClientListener}, this interface controls
 * the information flow between the network package, i.e. the {@link Client},
 * and the GUI components.
 * </p>
 * <p>
 * This interface provides the common methods {@link #clientConnect(String)} and
 * {@link #clientDisconnect()} for connection handling. Whenever the socket
 * connects or disconnects, these methods are called.
 * </p>
 * <p>
 * Furthermore, the {@code ClientListener} provides the methods
 * {@link #notification(int)}, {@link #notification(int, String)},
 * {@link #messageReceived(String, String)} and
 * {@link #userListChanged(HashMap)} which are being called when the associated
 * event is received from the server.
 * </p>
 * 
 * @author Markus Döllinger
 */
public interface ClientListener {

	/**
	 * This method is called after the client successfully connected to the
	 * server.
	 * 
	 * @param remoteAddress The address of the server
	 */
	public void clientConnect(String remoteAddress);

	/**
	 * This method is called when the clients disconnects from the server,
	 * either because it closed the connection or the server shut down.
	 */
	public void clientDisconnect();

	/**
	 * This method is called when a text message is received.
	 * 
	 * @param author The author of the message
	 * @param message The message itself
	 */
	public void messageReceived(String author, String message);

	/**
	 * This method is called when a generic notification was received. This
	 * notification does not contain any variable data like a user name.
	 * 
	 * @param type The type of the notification
	 * @see NotificationFrame
	 */
	public void notification(int type);

	/**
	 * This method is called when a notification was received. This notification
	 * does contain a user name of the client that caused this notification,
	 * i.e. he joined or left the chat.
	 * 
	 * @param type The type of the notification
	 * @param username The user name of the client that caused the event
	 * @see NotificationFrame
	 */
	public void notification(int type, String username);

	/**
	 * This method is called when the user list changes, i.e. a client connected
	 * or disconnected.
	 * 
	 * @param newUserList The new user list, a mapping of session id to user
	 *            name
	 */
	public void userListChanged(HashMap<Integer, String> newUserList);

}

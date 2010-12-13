package pxchat.net;

import java.util.HashMap;

/**
 * This interface controls the information flow between network and the GUI
 * components.
 * 
 * Whenever a socket event is raised, the appropriate method in this interface
 * is called.
 * 
 * @author Markus Döllinger
 */
public interface ClientListener {

	/**
	 * This method is called after the client successfully connected to the
	 * server.
	 */
	public void clientConnect(String remoteAddress);

	/**
	 * This method is called when the clients disconnects from the server,
	 * either because it closed the connection or the server shut down.
	 */
	public void clientDisconnect();

	/**
	 * This method is called when a message is received.
	 * 
	 * @param author The author of the message
	 * @param message The message itself
	 */
	public void messageReceived(String author, String message);

	/**
	 * @param type
	 */
	public void notification(int type);

	/**
	 * @param type
	 * @param username
	 */
	public void notification(int type, String username);
	
	/**
	 * This method is called when the user list changes, i.e. a client connected
	 * or disconnected
	 * 
	 * @param newUserList
	 */
	public void userListChanged(HashMap<Integer, String> newUserList);

}

package pxchat.net;

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
	public void clientConnect();

	/**
	 * This method is called when the clients disconnects from the server,
	 * either because it closed the connection or the server shut down.
	 */
	public void clientDisconnect();
}

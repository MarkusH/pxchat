/**
 * 
 */
package pxchat.net.tcp;

/**
 * The <code>TCPClientListener</code> interface contains the callbacks used to
 * react to client events.
 * 
 * @author Markus Döllinger
 */
public interface TCPClientListener {

	/**
	 * This method is called when the <code>client</code> disconnects.
	 * 
	 * @param client The source of the event
	 */
	public void clientDisconnect(CustomSocket client);

	/**
	 * This method is called when the <code>client</code> is connecting.
	 * 
	 * @param client The source of the event
	 */
	public void clientConnecting(CustomSocket client);

	/**
	 * This method is called when the <code>client</code> connected.
	 * 
	 * @param client The source of the event
	 */
	public void clientConnect(CustomSocket client);

	/**
	 * This method is called when the <code>client</code> receives
	 * <code>data</code>.
	 * 
	 * @param client The source of the event
	 * @param data The data received
	 */
	public void clientRead(CustomSocket client, Object data);
}

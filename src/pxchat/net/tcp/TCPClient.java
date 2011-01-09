/**
 * 
 */
package pxchat.net.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The TCP clients adds the {@link #connect(String, int)} method to the
 * {@link CustomSocket} in order for it to be used as a simple client.
 * 
 * @author Markus Döllinger
 */
public class TCPClient extends CustomSocket {

	/**
	 * Constructs a new TCP client.
	 * 
	 * @param tcpClientListener
	 */
	public TCPClient(TCPClientListener tcpClientListener) {
		super(tcpClientListener);
	}

	/**
	 * Connects to the specified host on the specified port.
	 * 
	 * @param host The host to connect to
	 * @param port The port to connect to
	 * @throws UnknownHostException If the host was not found
	 * @throws IOException If an I/O exception occurred
	 */
	public void connect(String host, int port) throws UnknownHostException, IOException {
		if ((socket == null) || (!this.connected)) {
			this.connected = false;
			tcpClientListener.clientConnecting(this);
			this.socket = new Socket(host, port);
			this.initCipher();
			this.connected = true;
			this.sendDisconnectedEvent = false;
			tcpClientListener.clientConnect(this);
			doRead();
		}
	}

	/**
	 * Disconnects the client.
	 */
	public void disconnect() {
		try {
			this.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

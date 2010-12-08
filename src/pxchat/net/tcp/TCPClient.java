/**
 * 
 */
package pxchat.net.tcp;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Markus Döllinger
 * 
 */

public class TCPClient extends CustomSocket {

	/**
	 * @param tcpClientListener
	 */
	public TCPClient(TCPClientListener tcpClientListener) {
		super(tcpClientListener);
	}

	public void connect(String host, int port) throws UnknownHostException,
												IOException {
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

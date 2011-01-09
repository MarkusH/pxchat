package pxchat.net.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

/**
 * This class implements a non-blocking server using the TCP protocol. It
 * accepts connecting clients and stores them in an internal list.
 * 
 * @author Markus Döllinger
 */
public class TCPServer {

	/**
	 * The accept thread waits until a new client connects and
	 * calls the callback method afterwards.
	 * 
	 * @author Markus Döllinger
	 */
	class AcceptThread extends Thread {

		CustomSocket socket;

		public AcceptThread() {
			this.setDaemon(true);
		}

		@Override
		public void run() {
			try {
				socket = new CustomSocket(serverSocket.accept(), clientListener);
			} catch (IOException e) {
				acceptCallback(e);
				return;
			}
			acceptCallback(socket);
		}
	}
	
	/**
	 * The underlying server socket.
	 */
	private ServerSocket serverSocket = null;
	
	/**
	 * A list of connected clients.
	 */
	private Vector<CustomSocket> clients = new Vector<CustomSocket>();

	/**
	 * The current accept thread.
	 */
	private AcceptThread acceptThread = null;
	
	/**
	 * <code>true</code> if the server is closing, <code>false</code> else
	 */
	private boolean closing = false;

	/**
	 * <code>true</code> if the server is listening, <code>false</code> else
	 */
	private boolean listening = false;

	/**
	 * The server listener registered with this server.
	 */
	private TCPServerListener tcpServerListener;

	/**
	 * The client listener of the connected TCP clients.
	 */
	private TCPClientListener clientListener = new TCPClientListener() {

		@Override
		public void clientClearToSend(CustomSocket client) {
			if (tcpServerListener != null)
				tcpServerListener.clientClearToSend(client);
		}

		@Override
		public void clientConnect(CustomSocket client) {
			throw new IllegalStateException(
					"TCPServer> A socket already connected" + "to the server reconnected.");
		}

		@Override
		public void clientConnecting(CustomSocket client) {
		}

		@Override
		public void clientDisconnect(CustomSocket client) {
			clients.remove(client);
			if (tcpServerListener != null)
				tcpServerListener.clientDisconnect(client);
		}

		@Override
		public void clientRead(CustomSocket client, Object data) {
			if (tcpServerListener != null)
				tcpServerListener.clientRead(client, data);
		}
	};

	/**
	 * Constructs a new <code>TCPServer</code> with a specified
	 * {@link TCPServerListener}.
	 * 
	 * @param tcpServerListener The listener associated with this server.
	 */
	public TCPServer(TCPServerListener tcpServerListener) {
		this.tcpServerListener = tcpServerListener;
	}

	/**
	 * The accept callback in case of a new client.
	 * 
	 * @param socket The new client
	 */
	private synchronized void acceptCallback(CustomSocket socket) {
		clients.add(socket);
		if (tcpServerListener != null)
			tcpServerListener.clientConnecting(socket);
		if (tcpServerListener != null)
			tcpServerListener.clientConnect(socket);
		doAccept();
	}

	/**
	 * The accept callback in case of an error.
	 * 
	 * @param e The error
	 */
	private synchronized void acceptCallback(IOException e) {
		if (closing) {
			closing = false;
		} else {
			e.printStackTrace();
		}
	}

	/**
	 * Closes this server and disconnects all clients.
	 * 
	 * @throws IOException If the server could not be closed successfully
	 */
	public synchronized void close() throws IOException {
		if (serverSocket != null) {
			if (this.listening) {
				closing = true;

				for (int i = clients.size() - 1; i >= 0; --i)
					clients.get(i).close();

				Thread.yield();

				while (clients.size() > 0) {
					Thread.yield();

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					Thread.yield();
				}

				if (acceptThread != null) {
					acceptThread.interrupt();
					acceptThread = null;
				}

				this.listening = false;

				serverSocket.close();

				Thread.yield();
			}
		}

		this.listening = false;
	}

	/**
	 * Starts a new AcceptThread
	 */
	private void doAccept() {
		acceptThread = new AcceptThread();
		acceptThread.start();
	}

	/**
	 * Lets this server listen on the specified <code>port</code>.
	 * 
	 * @param port The port to listen on.
	 * @throws IOException
	 */
	public synchronized void listen(int port) throws IOException {
		if ((serverSocket == null) || (!this.listening)) {
			this.listening = false;
			serverSocket = new ServerSocket(port);
			this.listening = true;
			doAccept();
		}
	}
}
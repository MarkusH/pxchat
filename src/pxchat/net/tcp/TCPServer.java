package pxchat.net.tcp;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

/**
 * This class implements a non-blocking server using
 * the TCP protocol. It accepts connecting clients and
 * stores them in an internal list.
 * 
 * @author Markus Döllinger
 */
public class TCPServer {

	private ServerSocket serverSocket = null;
	private Vector<CustomSocket> clients = new Vector<CustomSocket>();
	private AcceptThread acceptThread = null;

	private boolean closing = false;
	private boolean listening = false;

	private ServerListener serverListener;
	
	private ClientListener clientListener = new ClientListener() {

		@Override
		public void clientDisconnect(CustomSocket client) {
			clients.remove(client);
			if (serverListener != null)
				serverListener.clientDisconnect(client);
		}

		@Override
		public void clientRead(CustomSocket client, Object data) {
			if (serverListener != null)
				serverListener.clientRead(client, data);
		}

		@Override
		public void clientConnect(CustomSocket client) {
			throw new IllegalStateException("TCPServer> A socket already connected" +
					"to the server reconnected.");
		}

		@Override
		public void clientConnecting(CustomSocket client) {
		}
	};


	/**
	 * Constructs a new <code>TCPServer</code> with a specified
	 * {@link ServerListener}.
	 * 
	 * @param serverListener	The listener associated with this server.
	 */
	public TCPServer(ServerListener serverListener) {
		this.serverListener = serverListener;
	}

	/**
	 * Lets this server listen on the specified <code>port</code>.
	 * 
	 * @param port				The port to listen on.
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

	/**
	 * Closes this server and disonnects all clients.
	 * 
	 * @throws IOException
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
	 * Starts an AcceptThread
	 */
	private void doAccept() {
		acceptThread = new AcceptThread();
		acceptThread.start();	
	}

	private synchronized void acceptCallback(CustomSocket socket) {
		clients.add(socket);
		if (serverListener != null)
			serverListener.clientConnecting(socket);
		if (serverListener != null)
			serverListener.clientConnect(socket);
		doAccept();
	}

	private synchronized void acceptCallback(IOException e) {
		if (closing) {
			closing = false;
		} else {
			e.printStackTrace();
		}
	}

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
}
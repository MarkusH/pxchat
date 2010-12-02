package pxchat.net.tcp;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

/**
 * @author Markus Döllinger
 *
 */
public class TCPServer {

	private ServerSocket serverSocket = null;
	private Vector<CustomSocket> clients = new Vector<CustomSocket>();
	private AcceptThread acceptThread = null;

	private boolean closing = false;
	private boolean listening = false;

//	private IServerCallbacks serverCallbacks = null;
//	
//	private IClientCallbacks clientCallbacks = new IClientCallbacks() {
//
//		@Override
//		public void clientDisconnect(CustomSocket client) {
//			clients.remove(client);
//			System.out.println("Server> Removing client due to disconnect: " + client);
//			serverCallbacks.clientDisconnect(client);
//		}
//
//		@Override
//		public void clientRead(CustomSocket client, Object object) {
//			System.out.println("Server> Message received from " + client + ": " + object);
//			serverCallbacks.clientRead(client, object);
//		}
//
//		@Override
//		public void clientConnect(CustomSocket client) {
//			throw new IllegalStateException("Server> A already connected socket of the server reconnected.");
//		}
//
//		@Override
//		public void clientConnecting(CustomSocket client) {
//		}
//	};


	public TCPServer(/*IServerCallbacks serverCallbacks*/) {
//		this.serverCallbacks = serverCallbacks;
	}

	public synchronized void listen(int port) throws IOException {
		if ((serverSocket == null) || (!this.listening)) {
			this.listening = false;
			serverSocket = new ServerSocket(port);
			this.listening = true;
			doAccept();
		}
	}

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

	private void doAccept() {
		acceptThread = new AcceptThread();
		acceptThread.start();	
	}

	private synchronized void acceptCallback(CustomSocket socket) {
		clients.add(socket);
//		serverCallbacks.clientConnect(socket);
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
				socket = new CustomSocket(serverSocket.accept()/*, server.clientCallbacks*/);

			} catch (IOException e) {
				acceptCallback(e);
				return;
			}
			acceptCallback(socket);
		}
	}
}
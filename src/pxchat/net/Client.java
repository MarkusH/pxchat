package pxchat.net;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;

import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.core.FrameAdapterListener;
import pxchat.net.protocol.frames.AuthenticationFrame;
import pxchat.net.protocol.frames.Frame;
import pxchat.net.protocol.frames.NotificationFrame;
import pxchat.net.protocol.frames.UserListFrame;
import pxchat.net.protocol.frames.VersionFrame;
import pxchat.net.tcp.TCPClientListener;
import pxchat.net.tcp.CustomSocket;
import pxchat.net.tcp.TCPClient;

/**
 * This class implements the client for pxchat. Only one instance is available
 * to the application. It can be obtained by calling {@link #getInstance()}.
 * 
 * @author Markus Döllinger
 */
public final class Client {

	/**
	 * The underlying TCP client.
	 */
	private TCPClient client;

	/**
	 * The frame adapter controlling the data flow.
	 */
	private FrameAdapter frameAdapter;

	/**
	 * The client listener sending events to the GUI.
	 */
	private Vector<ClientListener> clientListeners = new Vector<ClientListener>();
	
	private String loginName = "";
	private String loginPassword = "";

	/**
	 * The TCP client listener receiving events from the underlying TCP client.
	 */
	private TCPClientListener tcpClientListener = new TCPClientListener() {

		@Override
		public void clientRead(CustomSocket client, Object data) {
			System.out.println(this + "> Message received from server: " + data);
			frameAdapter.receive(data);
		}

		@Override
		public void clientDisconnect(CustomSocket client) {
			System.out.println(this + "> Client disconnected");

			for (ClientListener listener : clientListeners)
				listener.clientDisconnect();
		}

		@Override
		public void clientConnect(CustomSocket client) {
			System.out.println(this + "> Conntected to server: " + client.getRemoteAddress());

			for (ClientListener listener : clientListeners)
				listener.clientConnect(client.getRemoteAddress());

			frameAdapter.getOutgoing().add(VersionFrame.getCurrent());
			frameAdapter.getOutgoing().add(new AuthenticationFrame(loginName, loginPassword));
			frameAdapter.send();
		}

		@Override
		public void clientConnecting(CustomSocket client) {
			System.out.println(this + "> Connecting to server");
			frameAdapter.reset();
		}
	};

	private FrameAdapterListener adapterListener = new FrameAdapterListener() {

		@Override
		public void process(FrameAdapter adapter) {
			System.out.println(this + " executes " + adapter.getIncoming());
			
			for (Frame frame : adapter.getIncoming()) {

				switch (frame.getId()) {
					
					/*
					 * 
					 */
					case Frame.ID_NOTIFICATION:
						NotificationFrame nf = (NotificationFrame) frame;
						for (ClientListener listener : clientListeners) {
							listener.notification(nf.getMessage());
						}
						break;
						

					/*
					 * 
					 */
					case Frame.ID_USERLIST:
						UserListFrame uf = (UserListFrame) frame;
						for (ClientListener listener : clientListeners) {
							listener.userListChanged(uf.getUserlist());
						}
						break;
				}
			}

			adapter.getIncoming().clear();
		}
	};

	/**
	 * Constructs a new client.
	 */
	private Client() {
		client = new TCPClient(tcpClientListener);
		frameAdapter = new FrameAdapter(client, adapterListener);
	}

	private static class Holder {
		public static final Client INSTANCE = new Client();
	}

	public static Client getInstance() {
		return Holder.INSTANCE;
	}

	/**
	 * Connects to the specified host on the defined port. It uses the login credentials to
	 * establish a connection.
	 * 
	 * @param host The host to connect to
	 * @param port The port of the end-point
	 * @param name The login name
	 * @param password The login password
	 * @throws UnknownHostException If the host cannot be found
	 * @throws IOException If there was an error concerning the connection
	 */
	public void connect(String host, int port, 
	                    String name, String password) throws UnknownHostException, IOException {
		this.loginName = name;
		this.loginPassword = password;
		client.connect(host, port);
	}

	/**
	 * Disconnects the client.
	 */
	public void disconnect() {
		client.disconnect();
	}

	/**
	 * Returns the connection state of the client.
	 * 
	 * @return <code>true</code> if it is connected
	 */
	public boolean isConnected() {
		return client.isConnected();
	}

	public void registerClientListener(ClientListener listener) {
		if (listener != null)
			clientListeners.add(listener);
	}

	public void removeClientListener(ClientListener listener) {
		clientListeners.remove(listener);
	}
}
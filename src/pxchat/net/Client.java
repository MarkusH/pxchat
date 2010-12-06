package pxchat.net;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;

import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.core.FrameAdapterListener;
import pxchat.net.protocol.frames.VersionFrame;
import pxchat.net.tcp.TCPClientListener;
import pxchat.net.tcp.CustomSocket;
import pxchat.net.tcp.TCPClient;

/**
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

	/**
	 * The TCP client listener receiving events from the underlying TCP client.
	 */
	private TCPClientListener tcpClientListener = new TCPClientListener() {

		@Override
		public void clientRead(CustomSocket client, Object data) {
			System.out
					.println(this + "> Message received from server: " + data);
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
			System.out.println(this + "> Conntected to server");
			
			for (ClientListener listener : clientListeners)
				listener.clientConnect();
				
			frameAdapter.getOutgoing().add(VersionFrame.getCurrent());
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
	 * Connects to the specified host on the defined port.
	 * 
	 * @param host
	 * @param port
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public void connect(String host, int port) throws UnknownHostException,
			IOException {
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
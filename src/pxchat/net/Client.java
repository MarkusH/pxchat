package pxchat.net;

import java.io.IOException;
import java.net.UnknownHostException;

import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.core.FrameAdapterListener;
import pxchat.net.protocol.frames.VersionFrame;
import pxchat.net.tcp.ClientListener;
import pxchat.net.tcp.CustomSocket;
import pxchat.net.tcp.TCPClient;

/**
 * 
 * @author Markus Döllinger
 */
public final class Client {

	private TCPClient client;
	private FrameAdapter frameAdapter;

	private ClientListener clientListener = new ClientListener() {

		@Override
		public void clientRead(CustomSocket client, Object data) {
			System.out.println(this + "> Message received from server: " + data);
			frameAdapter.receive(data);
		}

		@Override
		public void clientDisconnect(CustomSocket client) {
			System.out.println(this + "> Client disconnected");
		}

		@Override
		public void clientConnect(CustomSocket client) {
			System.out.println(this + "> Conntected to server");
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
		client = new TCPClient(clientListener);
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
	public void connect(String host, int port) throws UnknownHostException, IOException {
		client.connect(host, port);
	}

	/**
	 * Disconnects the client.
	 */
	public void disconnect() {
		client.disconnect();
	}

	public boolean isConnected() {
		return client.isConnected();
	}
}
package pxchat.net;

import java.awt.Color;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.core.FrameAdapterListener;
import pxchat.net.protocol.frames.AuthenticationFrame;
import pxchat.net.protocol.frames.Frame;
import pxchat.net.protocol.frames.ImageChunkFrame;
import pxchat.net.protocol.frames.ImageIDFrame;
import pxchat.net.protocol.frames.ImageStartFrame;
import pxchat.net.protocol.frames.ImageStopFrame;
import pxchat.net.protocol.frames.MessageFrame;
import pxchat.net.protocol.frames.NotificationFrame;
import pxchat.net.protocol.frames.UserListFrame;
import pxchat.net.protocol.frames.VersionFrame;
import pxchat.net.tcp.CustomSocket;
import pxchat.net.tcp.TCPClient;
import pxchat.net.tcp.TCPClientListener;
import pxchat.whiteboard.BackgroundFrame;

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
	 * The user list is a mapping of session id to user name of the clients
	 * currently connected to the server
	 */
	private HashMap<Integer, String> userList = new HashMap<Integer, String>();

	/**
	 * The client listener sending events to the GUI.
	 */
	private Vector<ClientListener> clientListeners = new Vector<ClientListener>();
	
	/**
	 * The client listener sending whiteboard related events to the GUI.
	 */
	private Vector<WhiteboardClientListener> whiteboardClientListeners = 
		new Vector<WhiteboardClientListener>();

	/**
	 * The user name of the client.
	 */
	private String loginName = "";

	/**
	 * The password of the client.
	 */
	private String loginPassword = "";

	/**
	 * The image id that will be used next.
	 */
	private int nextImageID = -1;
	
	private Vector<ImageReceiver> imgReceivers = new Vector<ImageReceiver>();

	/**
	 * A list of image senders that send local images to the server and thus too
	 * the connected clients.
	 */
	private Vector<ImageSender> imgSenders = new Vector<ImageSender>();

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
					 * A new notification was received from the server. A
					 * notification is send in the following cases:
					 * 
					 * - A user left or joined the chat 
					 * - The authentication failed 
					 * - A timeout occurred 
					 * - The version of this client is not compatible
					 */
					case Frame.ID_NOTIFICATION:
						NotificationFrame nf = (NotificationFrame) frame;
						if (nf.getType() == NotificationFrame.AUTH_FAIL || nf.getType() == NotificationFrame.TIMEOUT || nf
								.getType() == NotificationFrame.VERSION_FAIL) {
							for (ClientListener listener : clientListeners) {
								listener.notification(nf.getType());
							}
						} else
							if (nf.getType() == NotificationFrame.JOIN || nf.getType() == NotificationFrame.LEAVE) {
								for (ClientListener listener : clientListeners) {
									listener.notification(nf.getType(), nf.getUsername());
								}
							}
						break;

					/*
					 * A new user list was sent from the server. This occurs
					 * when a user joined or left the chat.
					 */
					case Frame.ID_USERLIST:
						UserListFrame uf = (UserListFrame) frame;
						userList = uf.getUserlist();
						for (ClientListener listener : clientListeners) {
							listener.userListChanged(uf.getUserlist());
						}
						break;

					/*
					 * A text message was received.
					 */
					case Frame.ID_MSG:
						MessageFrame mf = (MessageFrame) frame;
						for (ClientListener listener : clientListeners) {
							listener.messageReceived(userList.get(mf.getSessionId()), mf
									.getMessage());
						}
						break;

					/*
					 * A new image id was sent. This image id will be used when the user
					 * loads a new image from the local computer. This frame is sent immediately
					 * after connecting to the server, and after a image sender starts to transfer
					 * a new image.
					 */
					case Frame.ID_IMG_ID:
						ImageIDFrame idf = (ImageIDFrame) frame;
						nextImageID = idf.getImageID();
						System.out.println("Received image id");
						break;
						
						
					case Frame.ID_IMG_START:
						ImageStartFrame sf = (ImageStartFrame) frame;
						System.out.println("Received ImageStartFrame with id " + sf.getImageID());
						ImageReceiver newRecv = new ImageReceiver(sf);
						imgReceivers.add(newRecv);
						break;
						
					case Frame.ID_IMG_CHUNK:
						ImageChunkFrame cf = (ImageChunkFrame) frame;
						for (ImageReceiver recv : imgReceivers) {
							if (recv.process(adapter, cf))
								break;
						}
						break;
						
					case Frame.ID_IMG_STOP:
						ImageStopFrame spf = (ImageStopFrame) frame;
						
						Iterator<ImageReceiver> iterator = imgReceivers.iterator();
						while (iterator.hasNext()) {
							ImageReceiver receiver = iterator.next();
							if (receiver.process(adapter, spf)) {
								iterator.remove();
								break;
							}
						}
						
						System.out.println(imgReceivers);
						break;
						
					case Frame.ID_BACKGROUND:
						BackgroundFrame bf = (BackgroundFrame) frame;
						if (bf.getType() == BackgroundFrame.COLOR)
							for (WhiteboardClientListener listener : whiteboardClientListeners) {
								listener.backgroundChanged(bf.getColor());
							}
						else
							for (WhiteboardClientListener listener : whiteboardClientListeners) {
								listener.backgroundChanged(bf.getImageID());
							}
						break;
				}
			}

			adapter.getIncoming().clear();
		}

		@Override
		public void sending(FrameAdapter adapter) {
			System.out.println(adapter + "> sending");

			Iterator<ImageSender> iterator = imgSenders.iterator();
			while (iterator.hasNext()) {
				ImageSender sender = iterator.next();

				adapter.getOutgoing().add(sender.getNextFrame());
				if (sender.isFinished())
					iterator.remove();
			}
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

	/**
	 * Returns the sole instance of the TCP client.
	 * 
	 * @return The intstace of the TCP client
	 */
	public static Client getInstance() {
		return Holder.INSTANCE;
	}

	/**
	 * Connects to the specified host on the defined port. It uses the login
	 * credentials to establish a connection.
	 * 
	 * @param host The host to connect to
	 * @param port The port of the end-point
	 * @param name The login name
	 * @param password The login password
	 * @throws UnknownHostException If the host cannot be found
	 * @throws IOException If there was an error concerning the connection
	 */
	public void connect(String host, int port, String name, String password)
																			throws UnknownHostException,
																			IOException {
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

	public void registerListener(ClientListener listener) {
		if (listener != null)
			clientListeners.add(listener);
	}

	public void removeListener(ClientListener listener) {
		clientListeners.remove(listener);
	}
	
	public void registerListener(WhiteboardClientListener listener) {
		if (listener != null)
			whiteboardClientListeners.add(listener);
	}

	public void removeListener(WhiteboardClientListener listener) {
		whiteboardClientListeners.remove(listener);
	}

	/**
	 * @return the nextImageID
	 */
	public int getNextImageID() {
		return nextImageID;
	}

	/*
	 * Client commands
	 */
	public void sendMessage(String message) {
		if (isConnected()) {
			frameAdapter.getOutgoing().add(new MessageFrame(message));
			frameAdapter.send();
		}
	}

	public void sendImage(int imageID) {
		if (isConnected()) {
			imgSenders.add(new ImageSender(imageID));
			frameAdapter.send();
		}
	}
	
	public void sendChangeBackground(Color color) {
		if (isConnected()) {
			frameAdapter.getOutgoing().add(new BackgroundFrame(color));
			frameAdapter.send();
		}
	}
	
	public void sendChangeBackground(int imageID) {
		if (isConnected()) {
			frameAdapter.getOutgoing().add(new BackgroundFrame(imageID));
			frameAdapter.send();
		}
	}
}
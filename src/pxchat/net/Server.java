package pxchat.net;

import java.io.IOException;
import java.util.HashMap;

import pxchat.net.protocol.core.AuthFrameAdapter;
import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.core.FrameAdapterListener;
import pxchat.net.protocol.core.FrameQueue;
import pxchat.net.protocol.core.ServerFrameAdapter;
import pxchat.net.protocol.core.ServerFrameAdapterListener;
import pxchat.net.protocol.frames.AuthenticationFrame;
import pxchat.net.protocol.frames.Frame;
import pxchat.net.protocol.frames.ImageIDFrame;
import pxchat.net.protocol.frames.MessageFrame;
import pxchat.net.protocol.frames.NotificationFrame;
import pxchat.net.protocol.frames.SessionIDFrame;
import pxchat.net.protocol.frames.UserListFrame;
import pxchat.net.protocol.frames.VersionFrame;
import pxchat.net.tcp.CustomSocket;
import pxchat.net.tcp.TCPServer;
import pxchat.net.tcp.TCPServerListener;

/**
 * This class implements the server for pxchat. It uses a TCP server to listen
 * on a specified port and handles incoming data.
 * 
 * @author Markus Döllinger
 */
public class Server {

	/**
	 * The underlying TCP server
	 */
	private TCPServer server;

	/**
	 * The frame adapter used to control the data flow
	 */
	private ServerFrameAdapter serverFrameAdapter;

	private HashMap<String, String> authList = new HashMap<String, String>();
	private HashMap<Integer, String> userList = new HashMap<Integer, String>();
	
	private int nextImageID = 0;
	
	private synchronized int getNextImageID() {
		return nextImageID++;
	}

	/**
	 * The TCP server listener used to process events of the underlying server
	 * socket.
	 */
	private TCPServerListener tcpServerListener = new TCPServerListener() {

		@Override
		public void clientRead(CustomSocket client, Object data) {
			serverFrameAdapter.getAdapter(client).receive(data);
		}

		@Override
		public void clientDisconnect(CustomSocket client) {
			AuthFrameAdapter adapter = serverFrameAdapter.getAdapter(client);
			serverFrameAdapter.delete(adapter);
			System.out.println(this + "> Client with id " + adapter
					.getSessionID() + " disconnected.");
		}

		@Override
		public void clientConnect(CustomSocket client) {
			FrameAdapter adapter = serverFrameAdapter.getAdapter(client);
			System.out
					.println(this + "> new connection " + client + " --> " + adapter);
		}

		@Override
		public void clientConnecting(CustomSocket client) {
		}
	};

	/**
	 * The server frame adapter listener used to process events of the frame
	 * adapter.
	 */
	private ServerFrameAdapterListener serverFrameAdapterListener = new ServerFrameAdapterListener() {

		@Override
		public void destroyAdapter(ServerFrameAdapter serverAdapter,
									AuthFrameAdapter adapter) {
			String name = userList.get(adapter.getSessionID());
			if (name != null) {
				serverAdapter.broadcast(FrameQueue.from(new NotificationFrame(
						name + " left the chat")), false);
			}
			userList.remove(adapter.getSessionID());
			sendUserList();
		}

		@Override
		public void createAdapter(ServerFrameAdapter serverAdapter,
									AuthFrameAdapter adapter) {
			final AuthFrameAdapter fa = adapter;

			// Start a thread that waits for a few milliseconds. If the client
			// did not authenticate within this time frame, the connection is
			// terminated.
			Thread timeOut = new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						Thread.sleep(1500);
						if (!fa.isVersionVerified() || !fa.isAuthenticated()) {
							fa.getOutgoing()
									.add(new NotificationFrame(
											"Timeout waiting for authentication"));
							fa.send();
							fa.getSocket().close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			timeOut.setDaemon(true);
			timeOut.start();
		}
	};

	/**
	 * The frame adapter listener used for the sockets connected to the server.
	 */
	private FrameAdapterListener frameAdapterListener = new FrameAdapterListener() {

		@Override
		public void process(FrameAdapter fAdapter) {
			AuthFrameAdapter adapter = (AuthFrameAdapter) fAdapter;
			System.out
					.println(this + "> executes " + adapter.getIncoming() + " from " + adapter
							.getSocket());

			for (Frame frame : adapter.getIncoming()) {

				// disconnect if the version is not verified and the frame is
				// not a version frame
				if (!adapter.isVersionVerified() && !(frame instanceof VersionFrame)) {
					adapter.getOutgoing().add(
							new NotificationFrame(
									"The version was not verified"));
					adapter.send();
					adapter.disconnect();
					return;
				}

				// disconnect if the adapter is not authenticated and the frame
				// is not a authentication frame
				if (adapter.isVersionVerified() && !adapter.isAuthenticated() && !(frame instanceof AuthenticationFrame)) {
					adapter.getOutgoing().add(
							new NotificationFrame(
									"Authentication was not initiated"));
					adapter.send();
					adapter.disconnect();
					return;
				}

				switch (frame.getId()) {

					/*
					 * This is a dummy command that should not be used.
					 */
					case Frame.ID_NOP:
						break;

					/*
					 * The client sent a version frame. Usually this is sent
					 * immediately after the client connected to the server. If
					 * the version is compatible to the current version of this
					 * server, the client will receive a session id. If not, the
					 * connection is terminated.
					 * 
					 * TODO: If another frame is sent before the version frame
					 * arrives, or the version frame does not arrive after a
					 * specified timeout, the connection needs to be terminated.
					 */
					case Frame.ID_VERSION:
						VersionFrame vf = (VersionFrame) frame;
						if (!vf.isCompatible(VersionFrame.getCurrent())) {
							System.out
									.println(this + "> Version control unsuccessful.");
							adapter.getOutgoing()
									.add(new NotificationFrame(
											"Version control was unsuccessful"));
							adapter.send();
							adapter.disconnect();
						} else {
							System.out
									.println(this + "> Version control successful, send sessionID");
							adapter.setVersionVerified(true);
							adapter.getOutgoing().add(
									new SessionIDFrame(adapter.getSessionID()));
							adapter.getOutgoing().add(new ImageIDFrame(getNextImageID()));
							adapter.send();
						}
						break;

					case Frame.ID_AUTH:
						AuthenticationFrame af = (AuthenticationFrame) frame;
						String pwd = authList.get(af.getUsername());

						// reject access, if password is not matching (or null)
						// or if the user name
						// is already in use
						if (!af.getPassword().equals(pwd) || userList.values()
								.contains(af.getUsername())) {
							adapter.getOutgoing()
									.add(new NotificationFrame(
											"Authentification was unsuccessful"));
							adapter.send();
							adapter.disconnect();
						} else {
							adapter.setAuthenticated(true);
							serverFrameAdapter
									.broadcast(
											FrameQueue
													.from(new NotificationFrame(
															af.getUsername() + " joined the chat")),
											false);
							userList.put(adapter.getSessionID(),
									af.getUsername());
							sendUserList();
						}

						break;

					case Frame.ID_MSG:
						MessageFrame mf = (MessageFrame) frame;
						mf.setSessionId(adapter.getSessionID());
						serverFrameAdapter.broadcast(FrameQueue.from(mf), true,
								adapter.getSessionID());
						break;
				}
			}

			// clear all processed frames
			adapter.getIncoming().clear();
		}
	};

	/**
	 * Constructs a new server.
	 */
	public Server() {
		server = new TCPServer(tcpServerListener);
		serverFrameAdapter = new ServerFrameAdapter(serverFrameAdapterListener,
				frameAdapterListener);
	}

	/**
	 * Lets the server listen on the specified port. If the server is already
	 * listening, nothing is done.
	 * 
	 * @param port The port to listen on
	 * @throws IOException if an I/O error occurs when opening the socket
	 */
	public void listen(int port) throws IOException {
		server.listen(port);
	}

	/**
	 * Closes the server and disconnects all clients.
	 */
	public void close() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends the current user list to all clients
	 */
	private void sendUserList() {
		// TODO: only send this to verified clients
		FrameQueue queue = new FrameQueue();
		queue.add(new UserListFrame(this.userList));
		serverFrameAdapter.broadcast(queue, true);
	}

	/**
	 * Sets the authentication list of the server.
	 * 
	 * @param authList The new authentication list
	 */
	public void setAuthList(HashMap<String, String> authList) {
		this.authList = authList;
	}

	/**
	 * Returns the current user list of the server.
	 * 
	 * @return The user list
	 */
	public HashMap<Integer, String> getUserList() {
		return this.userList;
	}
}

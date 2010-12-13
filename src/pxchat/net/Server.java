package pxchat.net;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import pxchat.net.protocol.core.AuthFrameAdapter;
import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.core.FrameAdapterListener;
import pxchat.net.protocol.core.FrameQueue;
import pxchat.net.protocol.core.ServerFrameAdapter;
import pxchat.net.protocol.core.ServerFrameAdapterListener;
import pxchat.net.protocol.frames.AuthenticationFrame;
import pxchat.net.protocol.frames.Frame;
import pxchat.net.protocol.frames.ImageChunkFrame;
import pxchat.net.protocol.frames.ImageIDFrame;
import pxchat.net.protocol.frames.ImageStartFrame;
import pxchat.net.protocol.frames.ImageStopFrame;
import pxchat.net.protocol.frames.LockFrame;
import pxchat.net.protocol.frames.MessageFrame;
import pxchat.net.protocol.frames.NotificationFrame;
import pxchat.net.protocol.frames.SessionIDFrame;
import pxchat.net.protocol.frames.UserListFrame;
import pxchat.net.protocol.frames.VersionFrame;
import pxchat.net.tcp.CustomSocket;
import pxchat.net.tcp.TCPServer;
import pxchat.net.tcp.TCPServerListener;
import pxchat.whiteboard.BackgroundFrame;

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

	/**
	 * A mapping of user name to password used to authenticate with the server.
	 * Each pair can only be connected once.
	 */
	private HashMap<String, String> authList = new HashMap<String, String>();

	/**
	 * A mapping of session id to user name of the clients currently connected.
	 */
	private HashMap<Integer, String> userList = new HashMap<Integer, String>();

	/**
	 * A list of image receivers receiving and transferring images from and to
	 * clients.
	 */
	private Vector<ServerImageReceiver> imgReceivers = new Vector<ServerImageReceiver>();

	
	/**
	 * The current background of the whiteboard. Only the current frame has to
	 * be cached because setting the background does not have any side-effects.
	 */
	private BackgroundFrame backgroundCache;
	
	/**
	 * A queue of all draw commands in the correct order. Each new client will
	 * receive this queue in order to synchronize with the others.
	 */
	private FrameQueue paintObjectCache = new FrameQueue();

	/**
	 * The next image id that will be send the the client requesting it.
	 */
	private int nextImageID = 0;

	/**
	 * The TCP server listener used to process events of the underlying server
	 * socket.
	 */
	private TCPServerListener tcpServerListener = new TCPServerListener() {

		@Override
		public void clientClearToSend(CustomSocket client) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void clientConnect(CustomSocket client) {
			FrameAdapter adapter = serverFrameAdapter.getAdapter(client);
			System.out.println(this + "> new connection " + client + " --> " + adapter);
		}

		@Override
		public void clientConnecting(CustomSocket client) {
		}

		@Override
		public void clientDisconnect(CustomSocket client) {
			AuthFrameAdapter adapter = serverFrameAdapter.getAdapter(client);
			serverFrameAdapter.delete(adapter);
			System.out
					.println(this + "> Client with id " + adapter.getSessionID() + " disconnected.");
		}

		@Override
		public void clientRead(CustomSocket client, Object data) {
			// pass the event to the corresponding adapter
			serverFrameAdapter.getAdapter(client).receive(data);
		}
	};

	/**
	 * The server frame adapter listener used to process events of the frame
	 * adapter.
	 */
	private ServerFrameAdapterListener serverFrameAdapterListener = new ServerFrameAdapterListener() {

		@Override
		public void createAdapter(ServerFrameAdapter serverAdapter, AuthFrameAdapter adapter) {
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
							fa.getOutgoing().add(new NotificationFrame(NotificationFrame.TIMEOUT));
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

		@Override
		public void destroyAdapter(ServerFrameAdapter serverAdapter, AuthFrameAdapter adapter) {
			String name = userList.get(adapter.getSessionID());
			if (name != null) {
				serverAdapter.broadcast(FrameQueue.from(new NotificationFrame(
						NotificationFrame.LEAVE, name)), false);
			}
			userList.remove(adapter.getSessionID());
			sendUserList();
		}
	};

	/**
	 * The frame adapter listener used for the sockets connected to the server.
	 */
	private FrameAdapterListener frameAdapterListener = new FrameAdapterListener() {

		@Override
		public void process(FrameAdapter fAdapter) {
			AuthFrameAdapter adapter = (AuthFrameAdapter) fAdapter;
			System.out.println(this + "> executes " + adapter.getIncoming() + " from " + adapter
					.getSocket());

			for (Frame frame : adapter.getIncoming()) {

				// disconnect if the version is not verified and the frame is
				// not a version frame
				if (!adapter.isVersionVerified() && !(frame instanceof VersionFrame)) {
					adapter.getOutgoing()
							.add(new NotificationFrame(NotificationFrame.VERSION_FAIL));
					adapter.send();
					adapter.disconnect();
					return;
				}

				// disconnect if the adapter is not authenticated and the frame
				// is not a authentication frame
				if (adapter.isVersionVerified() && !adapter.isAuthenticated() && !(frame instanceof AuthenticationFrame)) {
					adapter.getOutgoing().add(new NotificationFrame(NotificationFrame.AUTH_FAIL));
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
					 */
					case Frame.ID_VERSION:
						VersionFrame vf = (VersionFrame) frame;
						if (!vf.isCompatible(VersionFrame.getCurrent())) {
							System.out.println(this + "> Version control unsuccessful.");
							adapter.getOutgoing().add(new NotificationFrame(NotificationFrame.VERSION_FAIL));
							adapter.send();
							adapter.disconnect();
						} else {
							System.out
									.println(this + "> Version control successful, send sessionID");
							adapter.setVersionVerified(true);
							adapter.getOutgoing().add(new SessionIDFrame(adapter.getSessionID()));
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
						if (!af.getPassword().equals(pwd) || userList.values().contains(af.getUsername())) {
							adapter.getOutgoing().add(new NotificationFrame(NotificationFrame.AUTH_FAIL));
							adapter.send();
							adapter.disconnect();
						} else {
							adapter.setAuthenticated(true);
							// synchronize the whiteboard
							adapter.getOutgoing().addAll(paintObjectCache);
							if (backgroundCache != null) {
								adapter.getOutgoing().add(backgroundCache);
							}
							serverFrameAdapter.broadcast(FrameQueue.from(new NotificationFrame(NotificationFrame.JOIN, af.getUsername())), false);
							userList.put(adapter.getSessionID(), af.getUsername());
							sendUserList();
						}

						break;

					case Frame.ID_MSG:
						MessageFrame mf = (MessageFrame) frame;
						mf.setSessionId(adapter.getSessionID());
						serverFrameAdapter.broadcast(FrameQueue.from(mf), true, adapter.getSessionID());
						break;

					case Frame.ID_IMG_START:
						ImageStartFrame sf = (ImageStartFrame) frame;
						System.out.println("Received ImageStartFrame with id " + sf.getImageID());
						adapter.getOutgoing().add(new ImageIDFrame(getNextImageID()));
						adapter.send();
						ServerImageReceiver newRecv = new ServerImageReceiver(sf, adapter, serverFrameAdapter);
						imgReceivers.add(newRecv);
						break;

					case Frame.ID_IMG_CHUNK:
						ImageChunkFrame cf = (ImageChunkFrame) frame;
						for (ImageReceiver recv : imgReceivers) {
							if (recv.process(adapter, cf)){
								break;
							}
						}
						break;

					case Frame.ID_IMG_STOP:
						ImageStopFrame spf = (ImageStopFrame) frame;

						Iterator<ServerImageReceiver> iterator = imgReceivers.iterator();
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
						backgroundCache = (BackgroundFrame) frame;
						serverFrameAdapter.broadcastToAuth(FrameQueue.from(frame), true);
						break;
						
					case Frame.ID_LOCK:
						LockFrame lf = (LockFrame) frame;
						serverFrameAdapter.broadcast(FrameQueue.from(lf), true, adapter.getSessionID());
						break;

					case Frame.ID_CIRCLE:
					case Frame.ID_ELLIPSE:
					case Frame.ID_RECT:
					case Frame.ID_LINE:
					case Frame.ID_POINT:
						paintObjectCache.add(frame);
						serverFrameAdapter.broadcastToAuth(FrameQueue.from(frame), true);
						break;
				}
			}

			// clear all processed frames
			adapter.getIncoming().clear();
		}

		@Override
		public void sending(FrameAdapter adapter) {
			System.out.println(adapter + "> sending");
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
	 * Generates a unique image id.
	 * 
	 * @return The next image id
	 */
	private synchronized int getNextImageID() {
		return nextImageID++;
	}

	/**
	 * Returns the current user list of the server.
	 * 
	 * @return The user list
	 */
	public HashMap<Integer, String> getUserList() {
		return this.userList;
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
}

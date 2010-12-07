package pxchat.net;

import java.io.IOException;

import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.core.FrameAdapterListener;
import pxchat.net.protocol.core.ServerFrameAdapter;
import pxchat.net.protocol.core.ServerFrameAdapterListener;
import pxchat.net.protocol.frames.Frame;
import pxchat.net.protocol.frames.SessionIDFrame;
import pxchat.net.protocol.frames.VersionFrame;
import pxchat.net.tcp.CustomSocket;
import pxchat.net.tcp.TCPServer;
import pxchat.net.tcp.TCPServerListener;

public class Server {
	
	private TCPServer server;
	private ServerFrameAdapter serverFrameAdapter;

	private TCPServerListener tcpServerListener = new TCPServerListener() {
		
		@Override
		public void clientRead(CustomSocket client, Object data) {
			serverFrameAdapter.getAdapter(client).receive(data);
		}
		
		@Override
		public void clientDisconnect(CustomSocket client) {
			int sessionID = serverFrameAdapter.getAdapter(client).getSessionID();
			System.out.println(this + "> Client with id " + sessionID + " disconnected.");
		}

		@Override
		public void clientConnect(CustomSocket client) {
			FrameAdapter adapter = serverFrameAdapter.getAdapter(client);
			System.out.println(this + "> new connection " + client + " --> " + adapter);
		}

		@Override
		public void clientConnecting(CustomSocket client) {
		}
	};
	
	private ServerFrameAdapterListener serverFrameAdapterListener = new ServerFrameAdapterListener() {
		
		@Override
		public void destroyAdapter(ServerFrameAdapter serverAdapter,
				FrameAdapter adapter) {
			
		}
		
		@Override
		public void createAdapter(ServerFrameAdapter serverAdapter,
		                          FrameAdapter adapter) {
		}
	};
	
	private FrameAdapterListener frameAdapterListener = new FrameAdapterListener() {
		
		@Override
		public void process(FrameAdapter adapter) {
			System.out.println(this + "> executes " + adapter.getIncoming() + " from " + adapter.getSocket());
			
			
			for (Frame frame : adapter.getIncoming()) {
			
				switch (frame.getId()) {
				
					case Frame.ID_NOP: 
						break;
						
					case Frame.ID_VERSION:
						VersionFrame vf = (VersionFrame) frame;
						if (!vf.isCompatible(VersionFrame.getCurrent())) {
							System.out.println(this + "> Version control unsuccessful.");
							adapter.disconnect();
						} else {
							System.out.println(this + "> Version control successful, send sessionID");
							adapter.getOutgoing().add(new SessionIDFrame(adapter.getSessionID()));
							adapter.send();
						}
						break;
				}
			}
			
			adapter.getIncoming().clear();
		}
	};
	
	/**
	 */
	public Server() {
		server = new TCPServer(tcpServerListener);
		serverFrameAdapter = new ServerFrameAdapter(serverFrameAdapterListener, frameAdapterListener);
	}
	
	/**
	 */
	public void listen(int port) {
		try {
			server.listen(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Disconnects the client.
	 */
	public void stopListening() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

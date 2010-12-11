package pxchat.net.protocol.core;

import java.io.IOException;

import pxchat.net.tcp.CustomSocket;

/**
 * This class represents the interface between a socket and the frame protocol.
 * It handles incoming and outgoing data by calling the <code>process</code>
 * event of the associated listener.
 * 
 * @author Markus Döllinger
 */
public class FrameAdapter {

	/**
	 * The socket associated with this adapter.
	 */
	protected CustomSocket socket;

	/**
	 * A unique id used to identify this adapter, or more generally, the
	 * associated socket.
	 */
	protected int sessionID;

	/**
	 * The incoming frames which were received by the socket.
	 */
	protected FrameQueue incoming;

	/**
	 * The outgoing frames, which will be send be the socket.
	 */
	protected FrameQueue outgoing;

	/**
	 * A listener for the callbacks of this adapter.
	 */
	private FrameAdapterListener listener;

	/**
	 * Disconnects the socket after processing the incoming queue.
	 */
	private boolean doDisconnect = false;

	/**
	 * Constructs a new frame adapter associated with the <code>socket</code>
	 * and the <code>listener</code>.
	 * 
	 * @param socket The socket associated with this adapter.
	 * @param listener The listener associated with this adapter.
	 */
	public FrameAdapter(CustomSocket socket, FrameAdapterListener listener) {
		this.socket = socket;
		this.listener = listener;
		this.sessionID = 0;
		incoming = new FrameQueue();
		outgoing = new FrameQueue();
	}

	/**
	 * Resets the adapter by clearing the incoming and outgoing frame queues.
	 * This method should be called before the client connects.
	 */
	public void reset() {
		incoming.clear();
		outgoing.clear();
		this.doDisconnect = false;
	}

	/**
	 * Immediately sends the specified frame queue.
	 * 
	 * @param queue The frame queue to send
	 * @return <code>true</code> if successful, <code>false</code> else
	 */
	protected boolean send(FrameQueue queue) {
		try {
			System.out.println(this + "> send " + queue);
			
			boolean res = socket.writeObject(queue);
			System.out.println("send done");
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sends the outgoing frame queue.
	 * 
	 * @return <code>true</code> if successful, <code>false</code> else
	 */
	public synchronized boolean send() {
		// add data
		if (listener != null) 
			listener.sending(this);
		
		if ((outgoing != null) && (outgoing.size() > 0)) {
			
			
			FrameQueue tmp = outgoing;
			boolean result = send(tmp);
			outgoing = new FrameQueue();
			
//			boolean result = send(outgoing);
			outgoing.clear();
			return result;
		}
		return true;
	}

	/**
	 * Receives data from the socket and processes it by calling the process
	 * event of the listener. This method does not wait for incoming data, it
	 * has to be passed in <code>object</code>.
	 * 
	 * @param object The incoming data
	 */
	public synchronized void receive(Object object) {
		FrameQueue buffer = (FrameQueue) object;
		incoming.addAll(buffer);
		if (listener != null) {
			listener.process(this);
		}
		if (doDisconnect) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Disconnects the socket after having processed the received data.
	 */
	public void disconnect() {
		this.doDisconnect = true;
	}

	/**
	 * Returns the session id of this adapter.
	 * 
	 * @return The session id
	 */
	public int getSessionID() {
		return this.sessionID;
	}

	/**
	 * Sets the session id of this adapter. This should only be called once
	 * after having received the session id from the server.
	 * 
	 * @param sessionID The session id to set
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	/**
	 * Sets the listener of this adapter.
	 * 
	 * @param listener The new listener
	 */
	public void setListener(FrameAdapterListener listener) {
		this.listener = listener;
	}

	/**
	 * Returns the incoming frame queue of this adapter.
	 * 
	 * @return The incoming frame queue
	 */
	public FrameQueue getIncoming() {
		return this.incoming;
	}

	/**
	 * Returns the outgoing frame queue of this adapter.
	 * 
	 * @return The outgoing frame queue.
	 */
	public FrameQueue getOutgoing() {
		return this.outgoing;
	}

	/**
	 * Returns the socket associated with this adapter
	 * 
	 * @return The socket of this adapter
	 */
	public CustomSocket getSocket() {
		return this.socket;
	}
}

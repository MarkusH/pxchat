package pxchat.net.protocol.core;

import java.util.Vector;

import pxchat.net.tcp.CustomSocket;

/**
 * This class represents the interface between a server socket and the frame
 * protocol. It handles incoming and outgoing data by calling the
 * <code>process</code> event of the associated listener.
 * 
 * @author Markus Döllinger
 */
public class ServerFrameAdapter extends Vector<AuthFrameAdapter> {

	/**
	 * The listener for this server adapter.
	 */
	private ServerFrameAdapterListener serverListener;

	/**
	 * The listener for the frame adapters of this server adapter.
	 */
	private FrameAdapterListener adapterListener;

	private int nextSessionID = 0;

	/**
	 * Constructs a new server frame adapter with the specified listeners for
	 * the adapter itself and the child frame adapters.
	 * 
	 * @param serverListener The server listener
	 * @param adapterListener The frame adapter listener
	 */
	public ServerFrameAdapter(ServerFrameAdapterListener serverListener,
								FrameAdapterListener adapterListener) {
		this.serverListener = serverListener;
		this.adapterListener = adapterListener;
	}

	/**
	 * Creates and returns a new unique session id. This method is thread-safe.
	 * 
	 * @return A new session id
	 */
	protected synchronized int getNewSessionID() {
		return ++nextSessionID;
	}

	/**
	 * Returns the index of the frame adapter associated with the specified
	 * socket. If there is no adapter registered for the socket, a new one is
	 * created.
	 * 
	 * @param socket The socket to search the index for
	 * @return The index of the socket
	 */
	public int indexOfSocket(CustomSocket socket) {

		int result = 0;
		while ((result < this.size()) && (this.get(result).getSocket() != socket))
			result++;

		// create a new frame adapter
		if (result == this.size()) {
			AuthFrameAdapter newAdapter = new AuthFrameAdapter(socket, adapterListener);
			newAdapter.setSessionID(getNewSessionID());

			this.add(newAdapter);
			result = this.indexOf(newAdapter);

			if (serverListener != null)
				serverListener.createAdapter(this, newAdapter);
		}

		return result;
	}

	/**
	 * Returns the adapter associated with the specified socket. If there is no
	 * adapter registered for the socket, a new one is created.
	 * 
	 * @param socket The socket to return the adapter for
	 * @return The frame adapter associated with the socket
	 */
	public AuthFrameAdapter getAdapter(CustomSocket socket) {
		return this.get(indexOfSocket(socket));
	}

	/**
	 * Returns the adapter associated with the specified session id.
	 * 
	 * @param sessionID The session id to return the adapter for
	 * @return The frame adapter associated with the socket
	 */
	public AuthFrameAdapter getAdapter(int sessionID) {
		return this.get(indexOfSessionID(sessionID));
	}

	/**
	 * Removes the frame adapter with the specified index from the server
	 * adapter.
	 * 
	 * @param index The index of the frame adapter
	 */
	public void delete(int index) {
		AuthFrameAdapter candidate = this.get(index);

		if (candidate != null) {
			if (serverListener != null)
				serverListener.destroyAdapter(this, candidate);

			this.remove(candidate);
			candidate = null;
		}
	}

	/**
	 * Removes the specified frame adapter from the server adapter.
	 * 
	 * @param adapter The frame adapter to remove
	 */
	public void delete(AuthFrameAdapter adapter) {
		this.delete(this.indexOf(adapter));
	}

	/**
	 * Returns the index of the frame adapter with the specified session id.
	 * 
	 * @param sessionID The session id of the adapter
	 * @return The index of the adapter
	 */
	public int indexOfSessionID(int sessionID) {
		for (AuthFrameAdapter adapter : this) {
			if (adapter.getSessionID() == sessionID)
				return this.indexOf(adapter);
		}
		return -1;
	}

	/**
	 * Broadcasts data to all frame adapters registered with this server adapter
	 * and returns the number of clients reached. If <code>immediate</code> is
	 * set, this method calls the {@link FrameAdapter#send()} method.
	 * 
	 * It is possible to pass a list of exceptions to the broadcast.
	 * 
	 * @param queue The data to enqueue in the outgoing buffer of the frame
	 *            adapters
	 * @param immediate If <code>true</code>, the data is send immediately
	 * @param exceptSessionID A list of session ids the queue should not be sent to
	 * @return The number of clients reached by the broadcast
	 */
	public int broadcast(FrameQueue queue, boolean immediate, int... exceptSessionID) {
		int count = 0;
LOOP:	for (AuthFrameAdapter adapter : this) {
			for (int i : exceptSessionID) {
				if (adapter.getSessionID() == i)
					continue LOOP;
			}
			adapter.getOutgoing().addAll(queue);
			if (immediate)
				adapter.send();
			count++;
		}
		return count;
	}
}
/**
 * 
 */
package pxchat.net;

import java.util.ArrayList;
import java.util.List;

import pxchat.net.protocol.core.AuthFrameAdapter;
import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.core.FrameQueue;
import pxchat.net.protocol.core.ServerFrameAdapter;
import pxchat.net.protocol.frames.Frame;
import pxchat.net.protocol.frames.ImageChunkFrame;
import pxchat.net.protocol.frames.ImageStartFrame;
import pxchat.net.protocol.frames.ImageStopFrame;

/**
 * An image receiver that forwards the incoming frames to a set of registered
 * receivers. This receiver is used in the server to distribute the images to
 * all connected clients.
 * 
 * @author Markus Döllinger
 */
public class ServerImageReceiver extends ImageReceiver {

	/**
	 * The session id of the sender
	 */
	private int sender;

	/**
	 * A list of session ids of the receivers.
	 */
	private List<Integer> receiverList;

	/**
	 * An array of session ids of the receivers.
	 */
	private int[] receivers;

	/**
	 * The server frame adapter.
	 */
	private ServerFrameAdapter server;

	/**
	 * Constructs a new server image receiver of the specified start frame, the
	 * sender and the server.
	 * 
	 * @param startFrame The image start frame
	 * @param sender The sender of the start frame
	 * @param server The server frame adapter
	 */
	public ServerImageReceiver(ImageStartFrame startFrame, FrameAdapter sender,
								ServerFrameAdapter server) {
		super(startFrame);
		this.sender = sender.getSessionID();
		this.server = server;

		// Fill the receiver list with all adapters that are authenticated
		receiverList = new ArrayList<Integer>();
		for (AuthFrameAdapter adapter : server.getAdapters()) {
			if (adapter.isAuthenticated() && adapter.getSessionID() != this.sender)
				receiverList.add(adapter.getSessionID());
		}
		this.receivers = toIntArray(receiverList);

		// Forward the start frame
		send(startFrame);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pxchat.net.ImageReceiver#process(pxchat.net.protocol.core.FrameAdapter,
	 * pxchat.net.protocol.frames.ImageChunkFrame)
	 */
	public boolean process(FrameAdapter adapter, ImageChunkFrame frame) {
		if (super.process(adapter, frame)) {
			send(frame);
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pxchat.net.ImageReceiver#process(pxchat.net.protocol.core.FrameAdapter,
	 * pxchat.net.protocol.frames.ImageStopFrame)
	 */
	public boolean process(FrameAdapter adapter, ImageStopFrame frame) {
		if (super.process(adapter, frame)) {
			send(frame);
			return true;
		}

		return false;
	}

	/**
	 * Sends the specified frame to all receivers registered with this receiver.
	 * 
	 * @param frame The frame to send
	 */
	private void send(Frame frame) {
		this.server.broadcastTo(FrameQueue.from(frame), true, receivers);
	}

	/**
	 * Turns a list of integers into an int array.
	 * 
	 * @param list The list to convert
	 * @return The converted int array
	 */
	private int[] toIntArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		int i = 0;
		for (Integer e : list)
			ret[i++] = e.intValue();
		return ret;
	}

	/**
	 * Returns a list of adapters that are authenticated and connected after
	 * this image transfer was initiated.
	 * 
	 * @return A list of adapters this image has to be sent to
	 */
	public List<FrameAdapter> getLateReceivers() {
		ArrayList<FrameAdapter> result = new ArrayList<FrameAdapter>();
		for (AuthFrameAdapter adapter : server.getAdapters()) {
			if (adapter.isAuthenticated() && adapter.getSessionID() != sender && !receiverList
					.contains(adapter.getSessionID())) {
				result.add(adapter);
			}
		}
		return result;
	}

}

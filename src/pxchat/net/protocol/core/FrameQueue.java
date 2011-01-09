package pxchat.net.protocol.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import pxchat.net.protocol.frames.Frame;

/**
 * This class stores {@link Frame} objects in a list. It is accessed as a queue
 * of incoming or outgoing frames. This class is thread-safe.
 * 
 * @author Markus Döllinger
 */
public class FrameQueue extends ConcurrentLinkedQueue<Frame> { // LinkedList<Frame>
	// {

	private static final long serialVersionUID = 7781171372961505171L;

	/**
	 * Constructs a new {@link FrameQueue} with the elements specified in the
	 * arguments. The first argument will be at the head of the queue.
	 * 
	 * @param frames The frames to add to the queue
	 * @return The created queue
	 */
	public static FrameQueue from(Frame... frames) {
		FrameQueue result = new FrameQueue();
		for (Frame frame : frames) {
			result.add(frame);
		}
		return result;
	}
}

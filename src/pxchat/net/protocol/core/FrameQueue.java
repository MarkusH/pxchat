package pxchat.net.protocol.core;

import java.util.LinkedList;

import pxchat.net.protocol.frames.Frame;

/**
 * This class stores {@link Frame} objects in a <code>LinkedList</code>. It is
 * accessed as a queue of incoming or outgoing frames.
 * 
 * @author Markus Döllinger
 */
public class FrameQueue extends LinkedList<Frame> {
	
	public static FrameQueue from(Frame... frames) {
		FrameQueue result = new FrameQueue();
		for (Frame frame : frames) {
			result.add(frame);
		}
		return result;
	}

}

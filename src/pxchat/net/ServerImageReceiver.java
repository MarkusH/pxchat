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
 * @author Markus Döllinger
 */
public class ServerImageReceiver extends ImageReceiver {

	private int sender;
	private int[] receivers;
	
	private ServerFrameAdapter server;
	
	/**
	 * @param startFrame
	 */
	public ServerImageReceiver(ImageStartFrame startFrame, 
	                           FrameAdapter sender, ServerFrameAdapter server) {
		super(startFrame);
		this.sender = sender.getSessionID();
		this.server = server;
		ArrayList<Integer> recv = new ArrayList<Integer>();
		for (AuthFrameAdapter adapter : server) {
			if (adapter.isAuthenticated() && adapter.getSessionID() != this.sender)
				recv.add(adapter.getSessionID());
		}
		this.receivers = toIntArray(recv);
		
		send(startFrame);
	}

	public boolean process(FrameAdapter adapter, ImageChunkFrame frame) {
		if (super.process(adapter, frame)) {
			send(frame);
			return true;
		}
		
		return false;
	}
	
	public boolean process(FrameAdapter adapter, ImageStopFrame frame) {
		if (super.process(adapter, frame)) {
			send(frame);
			return true;
		}
		
		return false;
	}
	
	private void send(Frame frame) {
		this.server.broadcastTo(FrameQueue.from(frame), true, receivers);
	}
	
	private int[] toIntArray(List<Integer> list)  {
	    int[] ret = new int[list.size()];
	    int i = 0;
	    for (Integer e : list)  
	        ret[i++] = e.intValue();
	    return ret;
	}

}

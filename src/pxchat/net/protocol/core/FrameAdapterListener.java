/**
 * 
 */
package pxchat.net.protocol.core;

/**
 * This interface contains the callbacks used by the {@link FrameAdapter}.
 * 
 * @author Markus Döllinger
 */
public interface FrameAdapterListener {

	/**
	 * This method is called when a frame adapter receives messages. By
	 * accessing the incoming queue of the adapter, the frames can be processed.
	 * 
	 * This method should delete the frames from the queue after having
	 * processed them.
	 * 
	 * @param adapter The source of the event.
	 */
	public void process(FrameAdapter adapter);
}

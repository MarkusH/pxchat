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

	/**
	 * This method is called prior to sending data on the connection. The event
	 * can be used to add data to the outgoing queue of the adapter.
	 * 
	 * @param adapter The adapter that will send data.
	 */
	public void sending(FrameAdapter adapter);
}

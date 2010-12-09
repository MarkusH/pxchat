package pxchat.net.protocol.core;

/**
 * This interface contains the callbacks used by the {@link ServerFrameAdapter}.
 * 
 * @author Markus Döllinger
 */
public interface ServerFrameAdapterListener {

	/**
	 * This method is called whenever the server adapter creates a new frame
	 * adapter.
	 * 
	 * @param serverAdapter The source of the event
	 * @param adapter The newly created frame adapter
	 */
	public void createAdapter(ServerFrameAdapter serverAdapter,
								AuthFrameAdapter adapter);

	/**
	 * This method is called whenever the server adapter destroys a frame
	 * adapter.
	 * 
	 * @param serverAdapter The source of the event
	 * @param adapter The adapter that will be destroyed
	 */
	public void destroyAdapter(ServerFrameAdapter serverAdapter,
								AuthFrameAdapter adapter);
}

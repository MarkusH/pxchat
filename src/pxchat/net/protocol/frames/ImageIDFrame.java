/**
 * 
 */
package pxchat.net.protocol.frames;

/**
 * This frame is used by the server to transfer a free image id to a client.
 * This frame is sent immediately after the client authenticates or the client
 * used the last image id. The latter case is detected by tracking when the
 * client creates a new image, i.e. an {@link ImageStartFrame} was sent.
 * 
 * @author Markus Döllinger
 */
public class ImageIDFrame extends Frame {

	private static final long serialVersionUID = 129262327670689747L;

	/**
	 * A free image id.
	 */
	private int imageID;

	/**
	 * Constructs a new image id frame with the specified id.
	 * 
	 * @param imageID A free image id.
	 */
	public ImageIDFrame(int imageID) {
		this.imageID = imageID;
		this.id = Frame.ID_IMG_ID;
	}

	/**
	 * @return the imageID
	 */
	public int getImageID() {
		return imageID;
	}

}

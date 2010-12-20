/**
 * 
 */
package pxchat.net.protocol.frames;

/**
 * This frame is used to indicate that an image transfer was successfully
 * terminated.
 * 
 * @author Markus Döllinger
 */
public class ImageStopFrame extends Frame {

	private static final long serialVersionUID = -5310012502629240737L;

	/**
	 * The image id associated with this frame.
	 */
	private int imageID;

	/**
	 * Constructs a new image stop frame with the associated image id.
	 * 
	 * @param imageID The image id
	 */
	public ImageStopFrame(int imageID) {
		this.id = Frame.ID_IMG_STOP;
		this.imageID = imageID;
	}

	/**
	 * @return the imageID
	 */
	public int getImageID() {
		return imageID;
	}

}

/**
 * 
 */
package pxchat.net.protocol.frames;

/**
 * This frame is used to initiate an image transfer.
 * 
 * @author Markus Döllinger
 */
public class ImageStartFrame extends Frame {

	private static final long serialVersionUID = -2757356207449858724L;

	/**
	 * The id of the new image.
	 */
	private int imageID;

	/**
	 * The amount of data of the image.
	 */
	private int size;

	/**
	 * Constructs a new image start frame with the associated id and size.
	 * 
	 * @param imageID The image id of the image
	 * @param size The size of the image
	 */
	public ImageStartFrame(int imageID, int size) {
		this.imageID = imageID;
		this.size = size;
		this.id = Frame.ID_IMG_START;
	}

	/**
	 * @return the imageID
	 */
	public int getImageID() {
		return imageID;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
}

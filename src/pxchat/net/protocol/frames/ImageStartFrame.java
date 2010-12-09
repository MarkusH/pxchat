/**
 * 
 */
package pxchat.net.protocol.frames;

/**
 * @author markus
 *
 */
public class ImageStartFrame extends Frame {

	private int imageID;
	private int size;
	
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

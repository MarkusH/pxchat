/**
 * 
 */
package pxchat.net.protocol.frames;

/**
 * This frame sends the next image id to a client
 * 
 * @author Markus Döllinger
 */
public class ImageIDFrame extends Frame {

	private int imageID;
	
	/**
	 * Constructs a new image id frame
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

/**
 * 
 */
package pxchat.net.protocol.frames;

/**
 * @author markus
 *
 */
public class ImageStopFrame extends Frame {

	private int imageID;
	
	/**
	 * 
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

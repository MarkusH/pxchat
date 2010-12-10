/**
 * 
 */
package pxchat.net.protocol.frames;

/**
 * The image sync frame is sent after each chunk of data.
 * 
 * @author Markus Döllinger
 */
public class ImageSyncFrame extends Frame {

	private int imageID;
	private int sessionID;
	
	/**
	 * 
	 */
	public ImageSyncFrame(int imageID) {
		this.id = Frame.ID_IMG_SYNC;
		this.imageID = imageID;
		this.sessionID = -1;
	}
	
	/**
	 * 
	 */
	public ImageSyncFrame(int imageID, int sessionID) {
		this.id = Frame.ID_IMG_SYNC;
		this.imageID = imageID;
		this.sessionID = sessionID;
	}

	public int getImageID() {
		return imageID;
	}

}

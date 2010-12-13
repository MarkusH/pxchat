/**
 * 
 */
package pxchat.net.protocol.frames;

/**
 * @author Markus Döllinger
 *
 */
public class ImageChunkFrame extends Frame {

	private int imageID;
	private byte data[];
	
	/**
	 * 
	 */
	public ImageChunkFrame(int imageID, byte data[]) {
		this.imageID = imageID;
		this.data = data;
		this.id = Frame.ID_IMG_CHUNK;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @return the imageID
	 */
	public int getImageID() {
		return imageID;
	}

}

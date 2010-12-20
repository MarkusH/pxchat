/**
 * 
 */
package pxchat.net.protocol.frames;

/**
 * This frame is used to transfer image chunks. A chunk is an array of bytes.
 * The image id associates the data with an image.
 * 
 * @author Markus Döllinger
 */
public class ImageChunkFrame extends Frame {

	private static final long serialVersionUID = -7806607771124029955L;

	/**
	 * The image id of the associated image.
	 */
	private int imageID;

	/**
	 * The data of the chunk.
	 */
	private byte data[];

	/**
	 * Constructs a new image chunk frame with the specified image id and data.
	 * 
	 * @param imageID The id of the associated image
	 * @param data The data of the chunk
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

/**
 * 
 */
package pxchat.net;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.frames.ImageChunkFrame;
import pxchat.net.protocol.frames.ImageStartFrame;
import pxchat.net.protocol.frames.ImageStopFrame;
import pxchat.whiteboard.ImageTable;

/**
 * This class implements an image receiver that is able to read images sent by
 * an {@link ImageSender}.
 * 
 * @author Markus Döllinger
 */
public class ImageReceiver {

	private int imageID;
	private ByteBuffer data;

	/**
	 * Constructs a new image receiver from an image start frame.
	 * 
	 * @param startFrame An image start frame
	 */
	public ImageReceiver(ImageStartFrame startFrame) {
		this.imageID = startFrame.getImageID();
		this.data = ByteBuffer.allocate(startFrame.getSize());
	}

	/**
	 * Processes an image chunk frame by copying the data it contains into the
	 * internal buffer. The image id of the frame has to be the same this
	 * receiver is registered for; in this case this method returns
	 * <code>true</code>. Otherwise nothing will be done and this method returns
	 * <code>false</code>.
	 * 
	 * @param adapter The adapter the frame was received from
	 * @param frame The incoming chunk frame
	 * @return <code>true</code> if the frame was processed
	 */
	public boolean process(FrameAdapter adapter, ImageChunkFrame frame) {
		if (frame.getImageID() == this.imageID) {
			data.put(frame.getData());
			return true;
		}

		return false;
	}

	/**
	 * Processes an image stop frame by completing the internal buffer and
	 * converting the data to a buffered image. The newly created image will be
	 * put into the {@link ImageTable}. The image id of the frame has to be the
	 * same this receiver is registered for; in this case this method returns
	 * <code>true</code>. Otherwise nothing will be done and this method returns
	 * <code>false</code>.
	 * 
	 * @param adapter The adapter the frame was received from
	 * @param frame The incoming stop frame
	 * @return <code>true</code> if the frame was processed
	 */
	public boolean process(FrameAdapter adapter, ImageStopFrame frame) {
		if (frame.getImageID() == this.imageID) {
			ByteArrayInputStream bai = new ByteArrayInputStream(data.array());
			BufferedImage img = null;
			try {
				img = ImageIO.read(bai);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (img != null) {
				ImageTable.getInstance().put(this.imageID, img);
			}
			return true;
		}
		return false;
	}

	/**
	 * @return the imageID
	 */
	public int getImageID() {
		return imageID;
	}
}

/**
 * 
 */
package pxchat.net;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import pxchat.net.protocol.core.FrameAdapter;
import pxchat.net.protocol.frames.Frame;
import pxchat.net.protocol.frames.ImageChunkFrame;
import pxchat.net.protocol.frames.ImageStartFrame;
import pxchat.net.protocol.frames.ImageStopFrame;
import pxchat.net.protocol.frames.ImageSyncFrame;
import pxchat.whiteboard.ImageTable;

/**
 * This class implements a sender that is able the transfer images to other
 * end-points. The data is split up in multiple chunks.
 * 
 * @author Markus Döllinger
 */
public class ImageSender {

	private static final int CHUNK_SIZE = 10 * 1024;

	private int imageID;
	private ByteBuffer data;

	boolean started = false;
	boolean finished = false;

	/**
	 * Constructs a new image sender with the specified image id.
	 * 
	 * @param imageID
	 */
	public ImageSender(int imageID) {
		this.imageID = imageID;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try {
			BufferedImage img = ImageTable.getInstance().get(imageID);
			String format = img.getAlphaRaster() == null ? "jpg" : "png";
			System.out.println(format);
			ImageIO.write(img, format, bao);

		} catch (IOException e) {
			e.printStackTrace();
		}
		this.data = ByteBuffer.wrap(bao.toByteArray());
	}

	/**
	 * Returns the next frame that has to be sent, or <code>null</code> if the
	 * sender is finished.
	 * 
	 * @return The next frame or <code>null</code>.
	 */
	public Frame getNextFrame() {
		if (!this.started) {
			this.started = true;
			return new ImageStartFrame(imageID, data.capacity());
		}

		if (data.position() != data.capacity()) {
			int size = Math.min(CHUNK_SIZE, data.capacity() - data.position());
			System.out.println(data.position() + " / " + data.capacity());
			byte chunk[] = new byte[size];
			data.get(chunk);
			return new ImageChunkFrame(imageID, chunk);
		}

		if (!this.finished) {
			this.finished = true;
			System.out.println("finished sending");
			return new ImageStopFrame(imageID);
		}

		return null;
	}

	/**
	 * Returns <code>true</code> if the sender is finished, <code>false</code>
	 * else.
	 * 
	 * @return <code>true</code> if the sender is finished
	 */
	public boolean isFinished() {
		return this.finished;
	}

}

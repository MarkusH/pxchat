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
 * @author Markus Döllinger
 */
public class ImageReceiver {
	
	private int imageID;
	private ByteBuffer data;
	
	private boolean finished = false;

	/**
	 * Constructs a new image receiver from an image start frame.
	 */
	public ImageReceiver(ImageStartFrame startFrame) {
		this.imageID = startFrame.getImageID();
		this.data = ByteBuffer.allocate(startFrame.getSize());
	}
	

	public boolean process(FrameAdapter adapter, ImageChunkFrame frame) {
		if (frame.getImageID() == this.imageID) {
			data.put(frame.getData());
			System.out.println("chunk received: " + data.position() + " / " + data.capacity());
			return true;
		}
		
		return false;
	}
	
	public boolean process(FrameAdapter adapter, ImageStopFrame frame) {
		System.out.println("test id");
		if (frame.getImageID() == this.imageID) {
			this.finished = true;
			System.out.println("finished");
			ByteArrayInputStream bai = new ByteArrayInputStream(data.array());
			BufferedImage img = null;
			try {
				img = ImageIO.read(bai);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (img != null) {
				ImageTable.getInstance().put(this.imageID, img);
				System.out.println("image loaded");
//				try {
//					ImageIO.write(img, "jpg", new File(Client.getInstance().isConnected() ? "client.jpg" : "server.jpg"));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			return true;
		}
		return false;
	}
}

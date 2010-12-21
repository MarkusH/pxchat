/**
 * 
 */
package pxchat.whiteboard;

import pxchat.net.protocol.frames.Frame;

/**
 * This frame causes the whiteboard to discard all paint objects except the
 * background. The board will thus be cleared.
 * 
 * @author Markus Döllinger
 */
public class ImageClearFrame extends Frame {

	private static final long serialVersionUID = 7478880660996424471L;

	/**
	 * Constructs a new image clear frame.
	 */
	public ImageClearFrame() {
		this.id = Frame.ID_CLEAR;
	}

}

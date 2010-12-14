package pxchat.whiteboard;

import java.awt.Graphics2D;

import pxchat.net.protocol.frames.Frame;

/**
 * <p>
 * This class is the basis for all objects that can be drawn onto the paint
 * board. Each paint object encapsulates both the data and the functionality
 * used to render and transfer the object.
 * </p>
 * 
 * For this end, <code>PaintObject</code> inherits from {@link Frame} and
 * provides a {@link #draw(Graphics2D)} method.
 * 
 * @author Markus Döllinger
 */
public abstract class PaintObject extends Frame {

	private static final long serialVersionUID = -4264581861776880013L;

	/**
	 * Draws this object using the specified graphics context.
	 * 
	 * @param g The context the object should be rendered with
	 */
	public abstract void draw(Graphics2D g);
}

/**
 * 
 */
package pxchat.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import pxchat.net.protocol.frames.Frame;

/**
 * This class represents a path on the paint board. A path is a sequence of
 * points where adjacent points are connected with a line.
 * 
 * @author Markus Döllinger
 */
public class FreeHandObject extends PrimitiveObject {

	private static final long serialVersionUID = -5642701319756386459L;
	
	/**
	 * The path representing the drawn shapes.
	 */
	private GeneralPath path;

	/**
	 * Constructs a new free hand object with the specified path, color and
	 * width.
	 * 
	 * @param color The color of the path
	 * @param width The width of the path
	 */
	public FreeHandObject(GeneralPath path, Color color, float width) {
		super(color, width);
		this.path = path;
		this.id = Frame.ID_FREEHAND;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pxchat.whiteboard.PaintObject#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		beginDraw(g);
		g.draw(path);
		endDraw(g);
	}

	/**
	 * @return the path
	 */
	public GeneralPath getPath() {
		return path;
	}

}

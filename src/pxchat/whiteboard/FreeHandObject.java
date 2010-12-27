/**
 * 
 */
package pxchat.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.Arrays;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof FreeHandObject) || !super.equals(obj))
			return false;

		FreeHandObject that = (FreeHandObject) obj;

		PathIterator i1 = this.path.getPathIterator(null);
		PathIterator i2 = that.getPath().getPathIterator(null);
		float[] c1 = new float[6];
		float[] c2 = new float[6];
		
		while (!i1.isDone() && !i2.isDone()) {
			if (i1.currentSegment(c1) == i2.currentSegment(c2)) {
				if (!Arrays.equals(c1, c2))
					return false;
			} else {
				return false;
			}
			i1.next();
			i2.next();
		}
		return i1.isDone() && i2.isDone();
	}
}

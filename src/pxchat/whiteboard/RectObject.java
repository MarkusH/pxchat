package pxchat.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import pxchat.net.protocol.frames.Frame;

/**
 * This class represents a rectangle on the paint board.
 * 
 * @author Markus Döllinger
 */
public class RectObject extends PrimitiveObject {

	private static final long serialVersionUID = 6904826041568834064L;

	/**
	 * The top left edge of the rectangle.
	 */
	private Point topLeft;

	/**
	 * The width of the rectangle.
	 */
	private int width;

	/**
	 * The height of the rectangle.
	 */
	private int height;

	/**
	 * Constructs a new rectangle object with the specified position,
	 * dimensions, color and stroke width.
	 * 
	 * @param topLeft The position of the rectangle
	 * @param width The width of the rectangle
	 * @param height The height of the rectangle
	 * @param color The color of the rectangle
	 * @param strokeWidth The stroke width
	 */
	public RectObject(Point topLeft, int width, int height, Color color, float strokeWidth) {
		super(color, strokeWidth);
		this.id = Frame.ID_RECT;
		this.topLeft = topLeft;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructs a new rectangle object with the specified position,
	 * dimensions, color and stroke width. Point1 and Point2 are interpreted
	 * diagonal points.
	 * 
	 * @param point1 The first point of the rectangle
	 * @param point2 The second point of the rectangle
	 * @param color The color of the rectangle
	 * @param width The width of the rectangle
	 */
	public RectObject(Point point1, Point point2, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_RECT;
		this.topLeft = new Point(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y));
		this.width = Math.abs(point1.x - point2.x);
		this.height = Math.abs(point1.y - point2.y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pxchat.whiteboard.PaintObject#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		beginDraw(g);
		g.drawRect(topLeft.x, topLeft.y, width, height);
		endDraw(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof RectObject) || !super.equals(obj))
			return false;

		RectObject that = (RectObject) obj;

		return this.topLeft.equals(that.topLeft) && 
				this.width == that.width && this.height == that.height;
	}
}

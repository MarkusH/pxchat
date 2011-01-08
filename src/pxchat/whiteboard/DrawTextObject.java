/**
 * 
 */
package pxchat.whiteboard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import pxchat.net.protocol.frames.Frame;

/**
 * This class represents a single line of text on the paint board.
 * 
 * @author Florian Bausch
 */
public class DrawTextObject extends PrimitiveObject {

	private static final long serialVersionUID = 5723446375188054368L;

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
	 * The text.
	 */
	private String text;

	/**
	 * The font for the text.
	 */
	private Font font;

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
	public DrawTextObject(Point topLeft, int width, int height, Color color, float strokeWidth,
							String text, Font font) {
		super(color, strokeWidth);
		this.id = Frame.ID_TEXT;
		this.topLeft = topLeft;
		this.width = width;
		this.height = height;
		this.text = text;
		this.font = font;
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
	public DrawTextObject(Point point1, Point point2, Color color, float width, String text,
							Font font) {
		super(color, width);
		this.id = Frame.ID_TEXT;
		this.topLeft = new Point(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y));
		this.width = Math.abs(point1.x - point2.x);
		this.height = Math.abs(point1.y - point2.y);
		this.text = text;
		this.font = font;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pxchat.whiteboard.PaintObject#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		beginDraw(g);
		g.setFont(this.font);
		g.drawString(this.text, this.topLeft.x, this.topLeft.y);
		endDraw(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof DrawTextObject) || !super.equals(obj))
			return false;

		DrawTextObject that = (DrawTextObject) obj;

		return this.topLeft.equals(that.topLeft) && this.width == that.width && this.height == that.height && 
			this.text.equals(that.text) && this.font.equals(that.font);
	}
}
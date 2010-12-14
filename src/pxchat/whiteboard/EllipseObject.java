package pxchat.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import pxchat.net.protocol.frames.Frame;

/**
 * This class represents an ellipse on the paint board.
 * 
 * @author Markus Döllinger
 */
public class EllipseObject extends PrimitiveObject {

	private static final long serialVersionUID = 2948630159159089998L;

	/**
	 * The top left point of the rectangle spanning the ellipse.
	 */
	private Point topLeft;

	/**
	 * The width of the rectangle spanning the ellipse.
	 */
	private int width;

	/**
	 * The height of the rectangle spanning the ellipse.
	 */
	private int height;

	private transient Shape ellipse;

	/**
	 * Constructs a new ellipse with the specified spanning rectangle, color and
	 * width.
	 * 
	 * @param topLeft The top left point of the spanning rectangle
	 * @param width The width of the spanning rectangle
	 * @param height The height of the spanning rectangle
	 * @param color The color of the ellipse
	 * @param strokeWidth The width of the ellipse
	 */
	public EllipseObject(Point topLeft, int width, int height, Color color, float strokeWidth) {
		super(color, strokeWidth);
		this.id = Frame.ID_ELLIPSE;
		this.topLeft = topLeft;
		this.width = width;
		this.height = height;
	}

	/**
	 * Constructs a new ellipse with the specified spanning rectangle, color and
	 * width.
	 * 
	 * @param point1 The first point of the spanning rectangle
	 * @param point2 The second point of the spanning rectangle
	 * @param color The color of the ellipse
	 * @param width The width of the ellipse
	 */
	public EllipseObject(Point point1, Point point2, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_ELLIPSE;
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
		//TODO: this may be moved to readObject, we will see what's better
		if (ellipse == null)
			this.ellipse = new Ellipse2D.Double(topLeft.x, topLeft.y, width, height);
		beginDraw(g);
		g.draw(this.ellipse);
		endDraw(g);
	}

}

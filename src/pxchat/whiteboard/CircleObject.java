package pxchat.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import pxchat.net.protocol.frames.Frame;

/**
 * This class represents a circle on the paint board.
 * 
 * @author Markus Döllinger
 */
public class CircleObject extends PrimitiveObject {

	private static final long serialVersionUID = -7317179066116853759L;

	/**
	 * The center of the circle.
	 */
	private Point center;

	/**
	 * The radius of the circle
	 */
	private int radius;

	/**
	 * A transient shape representing the circle. This has to be kept in sync
	 * with {@link #center} and {@link #radius}.
	 */
	private transient Shape circle;

	/**
	 * Constructs a new circle with the specified center, radius, color and
	 * width.
	 * 
	 * @param center The center of the circle
	 * @param radius The radius of the circle
	 * @param color The color of the circle
	 * @param width The width of the circle
	 */
	public CircleObject(Point center, int radius, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_CIRCLE;
		this.center = center;
		this.radius = radius;
	}

	/**
	 * Constructs a new circle with the specified center, point on the arc,
	 * color and width.
	 * 
	 * @param center The center of the circle
	 * @param arc A point on the arc of the circle
	 * @param color The color of the circle
	 * @param width The width of the circle
	 */
	public CircleObject(Point center, Point arc, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_CIRCLE;

		this.center = center;
		this.radius = (int) Math.round(
				Math.sqrt((center.x - arc.x) * (center.x - arc.x) +
						(center.y - arc.y) * (center.y - arc.y)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pxchat.whiteboard.PaintObject#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		beginDraw(g);
		if (this.circle == null)
			this.circle = new Ellipse2D.Double(center.x - radius,
					center.y - radius, 2.0 * radius, 2.0 * radius);
		g.draw(this.circle);
		endDraw(g);
	}

}

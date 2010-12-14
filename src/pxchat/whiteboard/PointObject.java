package pxchat.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import pxchat.net.protocol.frames.Frame;

/**
 * This class represents a single point on the paint board.
 * 
 * @author Markus Döllinger
 */
public class PointObject extends PrimitiveObject {

	private static final long serialVersionUID = 2495278088857606514L;

	/**
	 * The coordinates of the point.
	 */
	private Point point;

	/**
	 * Constructs a new point object with the specified coordinates, color and
	 * width.
	 * 
	 * @param point The coordinates of this point
	 * @param color The color of this point
	 * @param width The widh of this point
	 */
	public PointObject(Point point, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_POINT;
		this.point = point;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pxchat.whiteboard.PaintObject#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		beginDraw(g);
		g.drawRect(point.x, point.y, 1, 1);
		endDraw(g);
	}

}

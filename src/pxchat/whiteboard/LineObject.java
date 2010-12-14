package pxchat.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import pxchat.net.protocol.frames.Frame;

/**
 * This class represents a line on the paint board.
 * 
 * @author Markus Döllinger
 */
public class LineObject extends PrimitiveObject {

	private static final long serialVersionUID = -4133824612157832638L;

	/**
	 * The starting point of the line.
	 */
	private Point start;

	/**
	 * The end point of the line.
	 */
	private Point stop;

	/**
	 * Constructs a new line object with the specified start and end points,
	 * color and width.
	 * 
	 * @param point1 The starting point of the line
	 * @param point2 The end point of the line
	 * @param color The color of the line
	 * @param width The width of the line
	 */
	public LineObject(Point point1, Point point2, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_LINE;
		this.start = point1;
		this.stop = point2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pxchat.whiteboard.PaintObject#draw(java.awt.Graphics2D)
	 */
	@Override
	public void draw(Graphics2D g) {
		beginDraw(g);
		g.drawLine(start.x, start.y, stop.x, stop.y);
		endDraw(g);
	}

}

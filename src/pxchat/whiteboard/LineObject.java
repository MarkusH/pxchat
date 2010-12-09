package pxchat.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import pxchat.net.protocol.frames.Frame;

public class LineObject extends PrimitiveObject {

	private Point start, stop;

	public LineObject(Point point1, Point point2, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_LINE;
		this.start = point1;
		this.stop = point2;
	}

	@Override
	public void draw(Graphics2D g) {
		beginDraw(g);
		g.drawLine(start.x, start.y, stop.x, stop.y);
		endDraw(g);
	}

}

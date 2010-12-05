package pxchat.gui.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import pxchat.net.protocol.frames.Frame;

public class CircleObject extends PrimitiveObject {

	private Point topLeft;
	private int widthheight;

	public CircleObject(Point point1, Point point2, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_ELLIPSE;
		this.widthheight = Math.min(Math.abs(point1.x - point2.x),
				Math.abs(point1.y - point2.y));
		this.topLeft = new Point(
				point1.x - (point1.x <= point2.x ? 0 : widthheight),
				point1.y - (point1.y <= point2.y ? 0 : widthheight));
	}

	public CircleObject(Point topLeft, int width, int height, Color color,
						float strokeWidth) {
		super(color, strokeWidth);
		this.id = Frame.ID_RECT;
		this.topLeft = topLeft;
		this.widthheight = Math.min(width, height);
	}

	@Override
	public void draw(Graphics2D g) {
		beginDraw(g);
		g.drawOval(topLeft.x, topLeft.y, widthheight, widthheight);
		endDraw(g);
	}

}

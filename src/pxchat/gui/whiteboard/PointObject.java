package pxchat.gui.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import pxchat.net.frames.Frame;

public class PointObject extends PrimitiveObject {

	private Point point;
	
	public PointObject(Point point, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_POINT;
		this.point = point;
	}

	@Override
	public void draw(Graphics2D g) {
		beginDraw(g);
		g.drawRect(point.x, point.y, 0, 0);
		endDraw(g);
	}

}

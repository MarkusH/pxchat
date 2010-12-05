package pxchat.gui.whiteboard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import pxchat.net.protocol.frames.Frame;

public class CircleObject extends PrimitiveObject {

	private Point center;
	private int radius;

	private transient Shape circle;

	public CircleObject(Point center, Point arc, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_CIRCLE;

		this.center = center;
		this.radius = (int) Math
				.round(Math
						.sqrt((center.x - arc.x) * (center.x - arc.x) + (center.y - arc.y) * (center.y - arc.y)));
	}

	public CircleObject(Point center, int radius, Color color, float width) {
		super(color, width);
		this.id = Frame.ID_CIRCLE;
		this.center = center;
		this.radius = radius;
	}

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

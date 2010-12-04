package pxchat.gui.whiteboard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public abstract class PrimitiveObject extends PaintObject {

	private Color color;
	private transient BasicStroke stroke;
	private float width;

	public PrimitiveObject(Color color, float width) {
		this.color = color;
		this.width = width;
		this.stroke = new BasicStroke(width, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND, 1.0f);
	}

	public void beginDraw(Graphics2D g) {
		if (stroke == null)
			stroke = new BasicStroke(width, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND, 1.0f);
		g.setStroke(stroke);
		g.setColor(color);
	}

	public void endDraw(Graphics2D g) {

	}
}

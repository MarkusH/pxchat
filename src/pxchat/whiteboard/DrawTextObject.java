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
	 * The top left edge of the text.
	 */
	private Point topLeft;

	/**
	 * The text.
	 */
	private String text;

	/**
	 * The font for the text.
	 */
	private Font font;

	/**
	 * Constructs a new text object with the specified position, dimensions,
	 * color and stroke width.
	 * 
	 * @param topLeft The position of the text
	 * @param color The color of the text
	 * @param strokeWidth The stroke width
	 * @param text The text of this object
	 * @param font The associated font
	 */
	public DrawTextObject(Point topLeft, Color color, float strokeWidth, String text, Font font) {
		super(color, strokeWidth);
		this.id = Frame.ID_TEXT;
		this.topLeft = topLeft;
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

		return this.topLeft.equals(that.topLeft) && this.text.equals(that.text) && this.font
				.equals(that.font);
	}
}
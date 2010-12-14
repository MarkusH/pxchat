/**
 * 
 */
package pxchat.whiteboard;

import java.awt.Color;

import pxchat.net.protocol.frames.Frame;

/**
 * This frame is used to set a new background. This may either be a new color or
 * an image. The type is specified by the <code>type</code> attribute. The
 * constructor will set the type automatically.
 * 
 * @author Markus Döllinger
 */
public class BackgroundFrame extends Frame {

	private static final long serialVersionUID = 6239470367323490267L;

	/**
	 * A constant indicating that the background is a color.
	 */
	public static final int COLOR = 0;

	/**
	 * A constant indicating that the background is an image.
	 */
	public static final int IMAGE = 1;

	/**
	 * The background color.
	 */
	private Color color;

	/**
	 * The background image.
	 */
	private int imageID;

	/**
	 * The type of the background frame.
	 */
	private int type;

	/**
	 * Constructs a new background frame of type COLOR.
	 * 
	 * @param color The color of the background
	 */
	public BackgroundFrame(Color color) {
		this.id = ID_BACKGROUND;
		this.color = color;
		this.type = COLOR;
	}

	/**
	 * Constructs a new background frame of type IMAGE.
	 * 
	 * @param imageID The image id associated with the new background image
	 */
	public BackgroundFrame(int imageID) {
		this.id = ID_BACKGROUND;
		this.imageID = imageID;
		this.type = IMAGE;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return the imageID
	 */
	public int getImageID() {
		return imageID;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
}

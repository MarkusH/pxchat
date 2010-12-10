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

	public static final int COLOR = 0;
	public static final int IMAGE = 1;

	private Color color;
	private int imageID;
	private int type;

	/**
	 * Constructs a new background frame of type COLOR.
	 * 
	 * @param color
	 */
	public BackgroundFrame(Color color) {
		this.id = ID_BACKGROUND;
		this.color = color;
		this.type = COLOR;
	}


	/**
	 * Constructs a new background frame of type IMAGE.
	 * 
	 * @param imageID
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

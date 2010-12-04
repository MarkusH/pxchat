package pxchat.gui.whiteboard;

import java.awt.Graphics2D;

import pxchat.net.protocol.frames.Frame;

public abstract class PaintObject extends Frame {

	public PaintObject() {
		// TODO Auto-generated constructor stub
	}

	public abstract void draw(Graphics2D g);

}

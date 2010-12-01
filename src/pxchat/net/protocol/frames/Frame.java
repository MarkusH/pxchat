package pxchat.net.protocol.frames;

import java.io.Serializable;

public abstract class Frame implements Serializable {

	
	public static final int ID_NOP = 0;
	public static final int ID_VERSION = 1;
	public static final int ID_SID = 2;
	public static final int ID_MSG = 3;
	public static final int ID_AUTO = 4;
	
	public static final int ID_IMG = 10;
	
	public static final int ID_POINT = 100;
	public static final int ID_LINE = 101;
	public static final int ID_RECT = 102;
	public static final int ID_CIRCLE = 103;
	public static final int ID_IMGTEXT = 104;
	
	
	protected int id = ID_NOP;

	/**
	 * 
	 */
	public Frame() {
	}

	public int getId() {
		return this.id;
	}
}
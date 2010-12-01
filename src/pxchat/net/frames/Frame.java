package pxchat.net.frames;

import java.io.Serializable;

public abstract class Frame implements Serializable {

	
	public static final int ID_NOP = 0;
	public static final int ID_VERSION = 1;
	public static final int ID_SID = 2;
	public static final int ID_MSG = 3;
	
	
	public static final int ID_POINT = 100;
	
	
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
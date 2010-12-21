/**
 * 
 */
package pxchat.net;

import java.awt.Color;

import pxchat.whiteboard.PaintObject;

/**
 * This interface controls the communication between the client and the
 * whiteboard. The commands send over the network will cause the methods of this
 * interface to be invoked.
 * 
 * @author Markus Döllinger
 * @author Markus Holtermann
 */
public interface WhiteboardClientListener {

	/**
	 * This method is called whenever the background changes its color.
	 * 
	 * @param color The new background color
	 */
	public void backgroundChanged(Color color);

	/**
	 * This method is called whenever the background is set to an image.
	 * 
	 * @param imageID The id of the background image
	 */
	public void backgroundChanged(int imageID);

	/**
	 * This method is called whenever the whiteboard is locked or unlocked.
	 * 
	 * @param lock <code>true</code> if the board was locked, <code>false</code>
	 *            otherwise
	 */
	public void changeControlsLock(boolean lock);

	/**
	 * This method is called whenever a paint object was received.
	 * 
	 * @param object The new paint object
	 * 
	 * @see PaintObject
	 */
	public void paintObjectReceived(PaintObject object);

	/**
	 * This method is called whenever the client requests a repaint of the
	 * whiteboard.
	 */
	public void paintRequest();

	/**
	 * This method is called whenever the whiteboard was cleared.
	 */
	public void imageCleared();
}

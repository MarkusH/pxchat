/**
 * 
 */
package pxchat.net;

import java.awt.Color;

import pxchat.whiteboard.PaintObject;

/**
 * <p>
 * Together with the {@link ClientListener}, this interface controls the
 * information flow between the network package, i.e. the {@link Client}, and
 * the GUI components, especially the white board.
 * </p>
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
	 * This method is called whenever the white board is locked or unlocked.
	 * 
	 * @param lock <code>true</code> if the board was locked, <code>false</code>
	 *            otherwise
	 */
	public void changeControlsLock(boolean lock);

	/**
	 * This method is called whenever a paint object was received.
	 * 
	 * @param object The new paint object
	 * @see PaintObject
	 */
	public void paintObjectReceived(PaintObject object);

	/**
	 * This method is called whenever the client requests a repaint of the white
	 * board.
	 * 
	 * @param complete if <code>true</code>, all of the paint objects backed up
	 *            since the connection was established have to be drawn again
	 *            after clearing the board. If <code>false</code>, only the new
	 *            paint objects need to be drawn.
	 */
	public void paintRequest(boolean complete);

	/**
	 * This method is called whenever the white board was cleared.
	 */
	public void imageCleared();
}

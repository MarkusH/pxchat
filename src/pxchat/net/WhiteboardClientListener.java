/**
 * 
 */
package pxchat.net;

import java.awt.Color;

import pxchat.whiteboard.PaintObject;

/**
 * @author Markus Döllinger
 * @author Markus Holtermann
 */
public interface WhiteboardClientListener {

	public void backgroundChanged(Color color);
	public void backgroundChanged(int imageID);	
	
	public void changeControlsLock(boolean lock);
	
	public void paintObjectReceived(PaintObject object);
	
	public void paintRequest();
}

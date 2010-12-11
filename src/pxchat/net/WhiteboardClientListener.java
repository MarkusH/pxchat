/**
 * 
 */
package pxchat.net;

import java.awt.Color;

/**
 * @author Markus Döllinger
 */
public interface WhiteboardClientListener {

	public void backgroundChanged(Color color);
	public void backgroundChanged(int imageID);	
	
	public void paintRequest();
}

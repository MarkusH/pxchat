/**
 * 
 */
package pxchat.whiteboard;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

/**
 * This class implements a global image storage were each image has a unique
 * key.
 * 
 * @author Markus Döllinger
 */
public class ImageTable {

	private static class Holder {
		public static final ImageTable INSTANCE = new ImageTable();
	}

	public static ImageTable getInstance() {
		return Holder.INSTANCE;
	}

	private Hashtable<Integer, BufferedImage> table;

	private ImageTable() {
		this.table = new Hashtable<Integer, BufferedImage>();
	}

	public BufferedImage get(Integer key) {
		if (key == null)
			return null;
		return table.get(key);
	}

	public BufferedImage put(Integer key, BufferedImage image) {
		return table.put(key, image);
	}
	
	public void clear() {
		this.table.clear();
	}
}

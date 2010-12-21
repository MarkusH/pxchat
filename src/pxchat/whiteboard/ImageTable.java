/**
 * 
 */
package pxchat.whiteboard;

import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Set;

/**
 * This class implements a global image storage were each image has a unique
 * key.
 * 
 * @author Markus Döllinger
 */
public final class ImageTable {

	private static class Holder {
		public static final ImageTable INSTANCE = new ImageTable();
	}

	/**
	 * Returns the sole instance of the image table.
	 * 
	 * @return The image table
	 */
	public static ImageTable getInstance() {
		return Holder.INSTANCE;
	}

	/**
	 * The actual mapping of image id to image.
	 */
	private Hashtable<Integer, BufferedImage> table;

	/**
	 * Constructs a new image table.
	 */
	private ImageTable() {
		this.table = new Hashtable<Integer, BufferedImage>();
	}

	/**
	 * Looks up the specified key in order to find the associated image.
	 * 
	 * @param key The key associated with the image
	 * @return The image associated with the key, or <code>null</code> if none
	 *         was found.
	 */
	public BufferedImage get(Integer key) {
		if (key == null)
			return null;
		return table.get(key);
	}

	/**
	 * Puts the pair of key and image into the image table.
	 * 
	 * @param key The key of the image
	 * @param image The image to store
	 * @return The image stored in the table
	 */
	public BufferedImage put(Integer key, BufferedImage image) {
		return table.put(key, image);
	}
	
	/**
	 * Returns all keys in this table.
	 * 
	 * @return The keys
	 */
	public Set<Integer> keySet() {
		return table.keySet();
	}

	/**
	 * Clears the image list.
	 */
	public void clear() {
		this.table.clear();
	}
}

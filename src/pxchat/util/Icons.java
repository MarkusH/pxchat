package pxchat.util;

import java.io.File;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * This class can be used to load <code>Icons</code>. It automatically caches
 * the icons and thus prevents unnecessary instances of the same icon.
 * 
 * A folder can be set to detect the icons in a specific directory.
 * 
 * @see #setFolder(String)
 * 
 * @author Markus Döllinger
 */
public final class Icons {

	private static HashMap<String, Icon> icons = new HashMap<String, Icon>();
	private static String folder = "";

	/**
	 * Adds an icon if it is not already cached.
	 * 
	 * @param path The path to the icon
	 * @return The icon loaded or cached
	 */
	private static Icon _addIcon(String path) {
		File file = new File(path);
		Icon icon = icons.get(file.getName());
		if (icon != null)
			return icon;
		if (!file.exists())
			return null;
		try {
			icon = new ImageIcon(file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (icon != null)
			icons.put(file.getName(), icon);
		return icon;
	}

	/**
	 * Adds all images in this folder.
	 * 
	 * @param folder The folder name
	 */
	public static void addFolder(String folder) {
		File dir = new File(folder);
		if (dir.isDirectory()) {
			for (File file : dir.listFiles()) {
				if (file.isFile()) {
					addIcon(file.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Tries to add an icon in the current directory or in the directory set by
	 * {@link #setFolder(String)}.
	 * 
	 * @param path The path to the icon
	 * @return The icon loaded
	 */
	private static Icon addIcon(String path) {
		Icon result = _addIcon(path);
		if (result == null)
			result = _addIcon(folder + path);

		return result;
	}

	/**
	 * Searches for an icon. If it is already cached, it is returned. If there
	 * is no icon in the internal memory, this method tries to load it by
	 * searchin in the current path and in the folder set by
	 * {@link #setFolder(String)}.
	 * 
	 * @param icon The name of the icon
	 * @return The icon associated with the name
	 */
	public static Icon get(String icon) {
		Icon result = icons.get(icon);
		if (result == null)
			return addIcon(icon);
		return result;
	}

	/**
	 * All icons will be searched in this folder.
	 * 
	 * @param folder A folder name
	 */
	public static void setFolder(String folder) {
		Icons.folder = folder + (folder.endsWith("/") ? "" : "/");
	}

	private Icons() {
	}
}

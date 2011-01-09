package pxchat.util;

import java.io.File;

/**
 * A simple file filter for pictures which only allows files with a specific
 * extension
 * 
 * @author Florian Bausch
 */
public class PicFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

	/**
	 * The description text of the file chooser.
	 */
	private String description = "Images (.png, .jpg, .bmp)";

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File arg0) {
		String fName = arg0.getName();
		String ext = "";

		if (fName.contains(".")) {
			ext = fName.substring(fName.lastIndexOf("."));
		}

		return ext.equalsIgnoreCase(".png") || ext.equalsIgnoreCase(".jpg") || ext
				.equalsIgnoreCase(".bmp") || arg0.isDirectory();
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}
}

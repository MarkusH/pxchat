package pxchat.util;

import java.io.File;

/**
 * @author Florian Bausch
 * 
 */
public class PicFileFilter extends javax.swing.filechooser.FileFilter implements
		java.io.FileFilter {

	private String description = "Images (.png, .jpg, .bmp)";

	@Override
	/**
	 * @param arg0	File whose file name shall be filtered
	 * @return 		Returns boolean true, if file is accepted (else false)
	 */
	public boolean accept(File arg0) {
		String fName = arg0.getName();
		String ext = "";

		if (fName.contains(".")) {
			ext = fName.substring(fName.lastIndexOf("."));
		}

		return ext.equalsIgnoreCase(".png") || ext.equalsIgnoreCase(".jpg")
				|| ext.equalsIgnoreCase(".bmp") || arg0.isDirectory();
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return this.description;
	}
}

package pxchat.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import pxchat.gui.I18n;

/**
 * A simple file filter for xml files which only allows files with .xml as
 * extension
 * 
 * @author Florian Bausch
 */
public class XMLFileFilter extends FileFilter implements java.io.FileFilter {

	/**
	 * The description text of the file chooser.
	 */
	private String description = I18n.getInstance().getString("ffXML") + " (.xml)";

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File arg0) {
		String fName = arg0.getName();
		String ext = "";

		if (fName.contains(".")) {
			ext = fName.substring(fName.lastIndexOf("."));
		}

		return ext.equalsIgnoreCase(".xml") || arg0.isDirectory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

}

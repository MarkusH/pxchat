package pxchat.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import pxchat.net.Client;
import pxchat.whiteboard.ImageTable;
import pxchat.whiteboard.PaintObject;

public class PaintBoard extends JPanel {

	private Integer background = -1;
	private BufferedImage board;
	// private BufferedImage preview;

	private Vector<PaintObject> previewObjects = new Vector<PaintObject>();

	public PaintBoard() {
		this.board = null;
		// this.preview = null;
	}

	public void loadBackground(File file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(file);
		} catch (Exception e) {
			return;
		}
		
		background = Client.getInstance().getNextImageID();
		ImageTable.getInstance().put(background, img);
		this.setBackground(Color.WHITE);

		Client.getInstance().sendImage(background);
		
		
//		try {
//			loadBackgroundImage(ImageIO.read(file));
//		} catch (IOException e) {
//			loadBackgroundImage(null);
//		}
	}

//	public void loadBackgroundImage(BufferedImage img) {
//		if (img == null) {
//			this.background = new BufferedImage(getWidth(), getHeight(),
//					BufferedImage.TYPE_4BYTE_ABGR);
//			Graphics2D g = this.background.createGraphics();
//			Composite comp = g.getComposite();
//			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,
//					0.0f));
//			g.fillRect(0, 0, getWidth(), getHeight());
//			g.setComposite(comp);
//		} else {
//			this.background = img;
//		}
//	}
	
	public void loadBackground(Color c) {
		this.background = null;
		this.setBackground(c);
	}

	/**
	 * Clears the board
	 */
	public void clearBoard() {
		if (this.board == null)
			return;
		Graphics2D g = this.board.createGraphics();
		Composite comp = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setComposite(comp);
	}

	/**
	 * Saves the board to a <code>BufferedImage</code>
	 * 
	 * @return The current PaintBoard
	 */
	public BufferedImage saveImage() {
		BufferedImage result = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_4BYTE_ABGR);

		paintComponent(result.createGraphics());
		return result;
	}

	/**
	 * Updates the preview layer
	 */
	private void updatePreview(Graphics2D g) {
		// Graphics2D g = null;

		// // create a new image if the preview is null
		// if (this.preview == null) {
		// this.preview = new BufferedImage(getWidth(), getHeight(),
		// BufferedImage.TYPE_4BYTE_ABGR);
		// g = this.preview.createGraphics();
		// } else {
		// g = this.preview.createGraphics();
		//
		// // otherwise clear the image
		// Composite comp = g.getComposite();
		// g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,
		// 0.0f));
		// g.fillRect(0, 0, getWidth(), getHeight());
		// g.setComposite(comp);
		// }
		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);

		// render the preview objects
		for (PaintObject o : previewObjects) {
			o.draw(g);
		}
	}

	/**
	 * Updates the actual board
	 */
	private void updateBoard() {
		if (this.board == null)
			this.board = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics og) {
		super.paintComponent(og);
		Graphics2D g = (Graphics2D) og;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// draw background
//		if (this.background == null)
//			loadBackgroundImage(null);
//		g.drawImage(this.background, 0, 0, getWidth(), getHeight(), null);
		
		BufferedImage img = ImageTable.getInstance().get(background);
		if (img != null)
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);

		// draw board
		updateBoard();
		g.drawImage(this.board, 0, 0, null);

		// draw preview
		updatePreview(g);
		// g.drawImage(this.preview, 0, 0, null);
	}

	public Vector<PaintObject> getPreviewObjects() {
		return previewObjects;
	}
}

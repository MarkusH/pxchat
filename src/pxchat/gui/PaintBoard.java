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

	private static final long serialVersionUID = 4813675397624015717L;
	
	private Integer background = -1;
	private BufferedImage board;

	private Vector<PaintObject> previewObjects = new Vector<PaintObject>();

	private Vector<PaintObject> cache = new Vector<PaintObject>();

	public PaintBoard() {
		this.board = null;
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

	public Vector<PaintObject> getCache() {
		return cache;
	}

	public Vector<PaintObject> getPreviewObjects() {
		return previewObjects;
	}

	public void loadBackground(Color c) {
		this.background = null;
		this.setBackground(c);
	}

	public void loadBackground(File file) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(file);
		} catch (Exception e) {
			return;
		}
		
		int owidth = img.getWidth();
		int oheight = img.getHeight();
		
		float width = 0;
		float height = 0;
		float ratio = (float)img.getWidth()/(float)img.getHeight();
		boolean resized = false;
		float stdratio = (float) (4.0/3.0);
		
		if(ratio >= stdratio && owidth > 1024) {
			width = 1024;
			height = (1024/ratio);
			resized = true;
		}
		if(ratio < stdratio && !resized && (oheight > 768 || owidth > 1024)) {
			width = 768*ratio;
			height = 768;
			resized = true;
		}	

		background = Client.getInstance().getNextImageID();
		
		if (resized) {
			BufferedImage downSampleImg = new BufferedImage((int)width, (int)height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = downSampleImg.createGraphics();
			g.drawImage(img, 0, 0, (int)width, (int)height, null);
			g.dispose();

			ImageTable.getInstance().put(background, downSampleImg);
		} else {
			ImageTable.getInstance().put(background, img);
		}

		this.setBackground(Color.WHITE);

		Client.getInstance().sendChangeBackground(background);
		Client.getInstance().sendImage(background);
	}

	public void loadBackground(int imageID) {
		this.background = imageID;
		this.setBackground(Color.WHITE);
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

		BufferedImage img = ImageTable.getInstance().get(background);
		if (img != null)
			g.drawImage(img, 0, 0, getWidth(), getHeight(), null);

		// draw board
		updateBoard();
		g.drawImage(this.board, 0, 0, null);

		// draw preview
		updatePreview(g);
	}

	/**
	 * Saves the board to a <code>BufferedImage</code>
	 * 
	 * @return The current PaintBoard
	 */
	public BufferedImage saveImage() {
		BufferedImage result = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_BGR);

		paintComponent(result.createGraphics());
		return result;
	}

	/**
	 * Updates the actual board
	 */
	private synchronized void updateBoard() {
		if (this.board == null)
			this.board = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = this.board.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		System.out.println("update locking");
		synchronized (this) {
			System.out.println("update locked");
			for (PaintObject obj : cache) {
				obj.draw(g);
			}
			cache.clear();
		}
		System.out.println("update unlocked");

	}

	/**
	 * Updates the preview layer
	 */
	private void updatePreview(Graphics2D g) {
		// render the preview objects
		for (PaintObject o : previewObjects) {
			o.draw(g);
		}
	}
}

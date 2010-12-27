package pxchat.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import pxchat.net.Client;
import pxchat.net.ImageSender;
import pxchat.whiteboard.BackgroundFrame;
import pxchat.whiteboard.ImageTable;
import pxchat.whiteboard.PaintObject;

public class PaintBoard extends JPanel {

	private static final long serialVersionUID = 4813675397624015717L;

	private HashMap<String, Integer> sentBackgrounds = new HashMap<String, Integer>();

	private Integer background = -1;
	private BufferedImage board;

	private boolean doCompleteRepaint = false;

	private Vector<PaintObject> previewObjects = new Vector<PaintObject>();

	/**
	 * A list of paint objects that were added to the paint board after the last
	 * call to {@link #updateBoard()}. The elements of this list will be drawn
	 * and added to the backup list.
	 */
	private Vector<PaintObject> cache = new Vector<PaintObject>();

	/**
	 * A list of all paint objects that were added to the paint board. This list
	 * is used in the case of a complete redraw.
	 */
	private Vector<PaintObject> backup = new Vector<PaintObject>();

	public PaintBoard() {
		this.board = null;
	}

	/**
	 * Adds a new paint object to the paint board. This method should be called
	 * whenever the server sends a new paint object.
	 * 
	 * @param obj The paint object to add.
	 */
	public void add(PaintObject obj) {
		this.cache.add(obj);
	}

	/**
	 * Clears the board by making it completely transparent. If clearBackup is
	 * <code>true</code>, the backup is cleared.
	 * 
	 * @param clearBackup If <code>true</code>, the backup will be cleared too
	 */
	public void clearBoard(boolean clearBackup) {
		if (clearBackup)
			backup.clear();
		if (this.board == null)
			return;
		Graphics2D g = this.board.createGraphics();
		Composite comp = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setComposite(comp);
	}

	public Vector<PaintObject> getPreviewObjects() {
		return previewObjects;
	}

	/**
	 * Sets the background to the specified color
	 * 
	 * @param c The new background color
	 */
	public void loadBackground(Color c) {
		this.background = null;
		this.setBackground(c);
	}

	/**
	 * Loads the image specified by {@code file} and down-samples it to fit the
	 * board without wasting memory. The background is not set by this function,
	 * it merely sends a {@link BackgroundFrame} to the server. If the image was
	 * not already loaded, a new {@link ImageSender} will be added to the
	 * client.
	 * 
	 * @param file The file of the background image
	 */
	public void loadBackground(File file) {
		if (sentBackgrounds.get(file.getAbsolutePath()) == null) {
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
			float ratio = (float) img.getWidth() / (float) img.getHeight();
			boolean resized = false;
			float stdratio = (float) (4.0 / 3.0);

			if (ratio >= stdratio && owidth > 1024) {
				width = 1024;
				height = (1024 / ratio);
				resized = true;
			}
			if (ratio < stdratio && !resized && (oheight > 768 || owidth > 1024)) {
				width = 768 * ratio;
				height = 768;
				resized = true;
			}

			this.setBackground(Color.WHITE);

			background = Client.getInstance().getNextImageID();
			sentBackgrounds.put(file.getAbsolutePath(), background);

			if (resized) {
				BufferedImage downSampleImg = new BufferedImage((int) width, (int) height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = downSampleImg.createGraphics();
				g.drawImage(img, 0, 0, (int) width, (int) height, null);
				g.dispose();

				ImageTable.getInstance().put(background, downSampleImg);
			} else {
				ImageTable.getInstance().put(background, img);
			}

			this.setBackground(Color.WHITE);

			Client.getInstance().sendChangeBackground(background);
			Client.getInstance().sendImage(background);
		} else {
			this.setBackground(Color.WHITE);

			Client.getInstance().sendChangeBackground(sentBackgrounds.get(file.getAbsolutePath()));
		}
	}

	/**
	 * Sets the background to the image associated with the specified image id.
	 * 
	 * @param imageID The image id of the new background image
	 */
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

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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
			this.board = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = this.board.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		synchronized (this) {
			if (doCompleteRepaint) {
				System.out.println(backup);
				for (PaintObject obj : backup) {
					obj.draw(g);
				}
				doCompleteRepaint = false;
			}
			for (PaintObject obj : cache) {
				obj.draw(g);
				backup.add(obj);
			}
			cache.clear();
		}

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

	/**
	 * Repaints the component. If complete is <code>true</code>, the board will
	 * be cleared before all paint objects are painted. If complete is
	 * <code>false</code>, the paint objects added since the last call to
	 * {@code repaint} will be drawn.
	 * 
	 * @param complete Indicates whether a complete repaint should be performed
	 */
	public void repaint(boolean complete) {
		this.doCompleteRepaint = complete;
		if (complete)
			clearBoard(false);
		repaint();
	}
}

package pxchat.gui.whiteboard;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class PaintBoard extends JPanel {

	private BufferedImage background;
	private BufferedImage board;
	private BufferedImage preview;

	public PaintBoard() {
		this.background = null;
		this.board = null;
		this.preview = null;
	}

	public void loadBackground(File file) {
		try {
			loadBackgroundImage(ImageIO.read(file));
		} catch (IOException e) {
			loadBackgroundImage(null);
		}
	}

	public void loadBackgroundImage(BufferedImage img) {
		if (img == null) {
			this.background = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = this.background.createGraphics();
			Composite comp = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR,
					0.0f));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setComposite(comp);
		} else {
			this.background = img;
		}
	}

	public void clearImage() {
		if (this.board == null)
			return;
		Graphics2D g = this.board.createGraphics();
		Composite comp = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setComposite(comp);
	}

	public BufferedImage saveImage() {
		BufferedImage result = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_4BYTE_ABGR);

		paintComponent(result.createGraphics());
		return result;
	}

	private void updatePreview(Graphics2D g) {
		if (this.preview == null)
			this.preview = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);
	}

	private void updateBoard(Graphics2D g) {
		if (this.board == null)
			this.board = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics og) {
		super.paintComponent(og);
		Graphics2D g = (Graphics2D) og;

		// draw background
		if (this.background == null)
			loadBackgroundImage(null);
		g.drawImage(this.background, 0, 0, getWidth(), getHeight(), null);

		// draw board
		updateBoard(g);
		g.drawImage(this.board, 0, 0, null);

		// draw preview
		updatePreview(g);
		g.drawImage(this.preview, 0, 0, null);
	}
}

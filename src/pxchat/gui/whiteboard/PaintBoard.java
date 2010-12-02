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

	public PaintBoard() {
		this.background = null;
	}

	public void loadBackground(File file) {
		try {
			loadBackgroundImage(ImageIO.read(file));
		} catch (IOException e) {
			loadBackgroundImage(null);
		}
	}

	public void loadBackgroundImage(BufferedImage img)
	{
		if (img == null) {
			this.background = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g = this.background.createGraphics();
			Composite comp = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setComposite(comp);
		} else {
			this.background = img;
		}
	}

	public void paintComponent(Graphics og) {
		super.paintComponent(og);
		Graphics2D g = (Graphics2D) og;
		if (this.background == null)
			loadBackgroundImage(null);
		g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
	}
}

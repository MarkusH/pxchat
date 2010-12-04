/**
 * 
 */
package pxchat.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.print.attribute.standard.JobMessageFromOperator;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import pxchat.gui.whiteboard.PaintBoard;
import pxchat.util.PicFileFilter;

/**
 * @author Markus H.
 * 
 */
public class WhiteBoard extends JFrame {

	public static enum Tool {
		Circle, Ellipse, Eraser, Freehand, Line, Rectangle, Text
	};

	private int sizeX = 800;
	private int sizeY = 600;

	private PaintBoard paintBoard;
	private JPanel toolbar;
	private JToggleButton drawCircle, drawEllipse, drawEraser, drawFreehand,
			drawLine, drawRectangle, drawText, lockCanvas;
	private JButton drawColor, loadImage, saveImage, clearImage,
			loadBackground;

	private Tool tool = Tool.Freehand;
	private Color currentColor = Color.BLACK;
	private Boolean lock = false;

	public WhiteBoard() {
		super(I18n.getInstance().getString("wbTitle"));
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"./data/img/icon/whiteboard.png"));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setLayout(new BorderLayout());

		paintBoard = new PaintBoard();
		// paintBoard.setOpaque(false);
		paintBoard.setBackground(Color.white);
		paintBoard.setPreferredSize(new Dimension(sizeX, sizeY));

		final JPopupMenu popup = new JPopupMenu();
		JMenuItem backgroundMenuItem = new JMenuItem(I18n.getInstance()
				.getString("wbBackground"), new ImageIcon(
				"./data/img/icon/load-background-16.png"));
		backgroundMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadBackground();
			}
		});
		JMenuItem saveMenuItem = new JMenuItem(I18n.getInstance().getString(
				"wbSaveToFile"), new ImageIcon("./data/img/icon/save-16.png"));
		saveMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveImage();

			}
		});

		JMenuItem clearMenuItem = new JMenuItem(I18n.getInstance().getString(
				"wbClear"), new ImageIcon("./data/img/icon/clear-16.png"));
		clearMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearImage();

			}
		});

		popup.add(backgroundMenuItem);
		popup.add(saveMenuItem);
		popup.add(clearMenuItem);

		paintBoard.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		toolbar = new JPanel();
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.PAGE_AXIS));
		toolbar.setPreferredSize(new Dimension(100, sizeY));

		drawCircle = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-circle.png"));
		drawCircle.setToolTipText(I18n.getInstance().getString("wbCircle"));
		drawCircle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Circle;

			}
		});
		drawColor = new JButton("", new ImageIcon(
				"./data/img/icon/draw-color.png"));
		drawColor.setToolTipText(I18n.getInstance().getString("wbChangeColor"));
		drawColor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color newColor = JColorChooser.showDialog(WhiteBoard.this, I18n
						.getInstance().getString("ccDialog"), currentColor);
				if (newColor != null) {
					currentColor = newColor;
				}
			}
		});
		drawEllipse = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-ellipse.png"));
		drawEllipse.setToolTipText(I18n.getInstance().getString("wbEllipse"));
		drawEllipse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Ellipse;

			}
		});
		drawEraser = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-eraser.png"));
		drawEraser.setToolTipText(I18n.getInstance().getString("wbEraser"));
		drawEraser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Eraser;

			}
		});
		drawFreehand = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-freehand.png"));
		drawFreehand.setToolTipText(I18n.getInstance().getString("wbPencil"));
		drawFreehand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Freehand;

			}
		});
		drawLine = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-line.png"));
		drawLine.setToolTipText(I18n.getInstance().getString("wbLine"));
		drawLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Line;

			}
		});
		drawRectangle = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-rectangle.png"));
		drawRectangle.setToolTipText(I18n.getInstance()
				.getString("wbRectangle"));
		drawRectangle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Rectangle;

			}
		});
		drawText = new JToggleButton("", new ImageIcon(
				"./data/img/icon/draw-text.png"));
		drawText.setToolTipText(I18n.getInstance().getString("wbText"));
		drawText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tool = Tool.Text;

			}
		});
		lockCanvas = new JToggleButton("", new ImageIcon(
				"./data/img/icon/lock.png"));
		lockCanvas.setToolTipText(I18n.getInstance().getString("wbLockCanvas"));
		lockCanvas.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (lockCanvas.isSelected()) {
					lockCanvas.setToolTipText(I18n
							.getInstance().getString("wbUnlockCanvas"));
					lockCanvas.setIcon(new ImageIcon(
							"./data/img/icon/unlock.png"));
				} else {
					lockCanvas.setToolTipText(I18n
							.getInstance().getString("wbLockCanvas"));
					lockCanvas.setIcon(new ImageIcon(
							"./data/img/icon/lock.png"));
				}
			}
		});
		loadImage = new JButton("", new ImageIcon(
				"./data/img/icon/load-image.png"));
		loadImage.setToolTipText(I18n.getInstance().getString("wbInsertImage"));
		loadImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				insertImage();
			}
		});
		saveImage = new JButton("", new ImageIcon("./data/img/icon/save.png"));
		saveImage.setToolTipText(I18n.getInstance().getString("wbSaveToFile"));
		saveImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveImage();
			}
		});

		clearImage = new JButton("", new ImageIcon("./data/img/icon/clear.png"));
		clearImage.setToolTipText(I18n.getInstance().getString("wbClear"));
		clearImage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearImage();

			}
		});

		loadBackground = new JButton("", new ImageIcon(
				"./data/img/icon/load-background.png"));
		loadBackground.setToolTipText(I18n.getInstance().getString(
				"wbBackground"));
		loadBackground.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadBackground();
			}
		});
		toolbar.add(drawColor);
		toolbar.add(drawFreehand);
		toolbar.add(drawLine);
		toolbar.add(drawRectangle);
		toolbar.add(drawCircle);
		toolbar.add(drawEllipse);
		toolbar.add(drawText);
		toolbar.add(drawEraser);
		toolbar.add(lockCanvas);
		toolbar.add(loadImage);
		toolbar.add(saveImage);
		toolbar.add(clearImage);
		toolbar.add(loadBackground);

		ButtonGroup g = new ButtonGroup();
		g.add(drawCircle);
		g.add(drawEllipse);
		g.add(drawEraser);
		g.add(drawFreehand);
		g.add(drawLine);
		g.add(drawRectangle);
		g.add(drawText);

		this.getContentPane().add(paintBoard, BorderLayout.CENTER);
		this.getContentPane().add(toolbar, BorderLayout.WEST);
		this.pack();

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width - getSize().width) / 2,
				(d.height - getSize().height) / 2);
	}

	private void clearImage() {
		paintBoard.clearImage();
	}

	private void insertImage() {
		// TODO insert image
	}

	private void saveImage() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new PicFileFilter());
		if (fc.showSaveDialog(WhiteBoard.this) == JFileChooser.APPROVE_OPTION) {
			String format = "png";
			String name = fc.getSelectedFile().getName();
			if (name.contains(".")) {
				format = name.substring(name.lastIndexOf(".") + 1);
			}
			try {
				ImageIO.write(paintBoard.saveImage(), format, fc.getSelectedFile());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(WhiteBoard.this, e.getMessage());
			}
		}
	}

	private void loadBackground() {
		// TODO need some option to set the background to a specific color
		// 		1) Display a dialog to choose "Image" or "Color"
		//		2) Add an additional button to the toolbar
		// 		3) Something else
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new PicFileFilter());
		if (fc.showOpenDialog(WhiteBoard.this) == JFileChooser.APPROVE_OPTION) {
			paintBoard.loadBackground(fc.getSelectedFile());
			paintBoard.repaint();
		}
	}

	public void lockControls(Boolean lock) {
		this.lock = lock;
		drawCircle.setEnabled(!lock);
		drawEllipse.setEnabled(!lock);
		drawEraser.setEnabled(!lock);
		drawFreehand.setEnabled(!lock);
		drawLine.setEnabled(!lock);
		drawRectangle.setEnabled(!lock);
		drawText.setEnabled(!lock);
		lockCanvas.setEnabled(!lock);
		drawColor.setEnabled(!lock);
		loadImage.setEnabled(!lock);
		clearImage.setEnabled(!lock);
		loadBackground.setEnabled(!lock);
	}

	public void lockControls() {
		lockControls(true);
	}

	public void unlockControls() {
		lockControls(false);
	}
}

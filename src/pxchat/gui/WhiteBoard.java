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

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import pxchat.gui.whiteboard.PaintBoard;

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
	private JButton drawColor, loadImage, saveImage, clearImage, loadBackground;

	private Tool tool = Tool.Freehand;
	private Color currentColor = Color.BLACK;

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
				insertImage();

			}
		});
		popup.add(backgroundMenuItem);
		popup.add(new JMenuItem(I18n.getInstance().getString("wbSaveToFile"),
				new ImageIcon("./data/img/icon/save-16.png")));
		popup.add(new JMenuItem(I18n.getInstance().getString("wbClear"),
				new ImageIcon("./data/img/icon/clear-16.png")));

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
				if (WhiteBoard.this.lockCanvas.getModel().isSelected()) {
					WhiteBoard.this.lockCanvas.setToolTipText(I18n
							.getInstance().getString("wbUnlockCanvas"));
					WhiteBoard.this.lockCanvas.setIcon(new ImageIcon(
							"./data/img/icon/unlock.png"));
				} else {
					WhiteBoard.this.lockCanvas.setToolTipText(I18n
							.getInstance().getString("wbLockCanvas"));
					WhiteBoard.this.lockCanvas.setIcon(new ImageIcon(
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
				insertImage();
			}
		});
		clearImage = new JButton("", new ImageIcon("./data/img/icon/clear.png"));
		clearImage.setToolTipText(I18n.getInstance().getString("wbClear"));
		
		loadBackground = new JButton("", new ImageIcon(
				"./data/img/icon/load-background.png"));
		loadBackground.setToolTipText(I18n.getInstance().getString(
				"wbBackground"));
		loadBackground.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Load a background image to a bottom layer
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

	private void insertImage() {
		JFileChooser fc = new JFileChooser();
		if (fc.showOpenDialog(WhiteBoard.this) == JFileChooser.APPROVE_OPTION) {
			paintBoard.loadBackground(fc.getSelectedFile());
			paintBoard.repaint();
		}
	}
}
